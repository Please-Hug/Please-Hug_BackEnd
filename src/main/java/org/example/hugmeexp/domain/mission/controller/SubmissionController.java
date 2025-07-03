package org.example.hugmeexp.domain.mission.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.hugmeexp.domain.mission.dto.request.SubmissionFeedbackRequest;
import org.example.hugmeexp.domain.mission.dto.response.SubmissionResponse;
import org.example.hugmeexp.domain.mission.enums.FileUploadType;
import org.example.hugmeexp.domain.mission.exception.SubMissionInternalException;
import org.example.hugmeexp.domain.mission.exception.SubmissionNotFoundException;
import org.example.hugmeexp.domain.mission.service.SubmissionService;
import org.example.hugmeexp.domain.mission.util.FileUploadUtils;
import org.example.hugmeexp.global.common.response.Response;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Tag(name = "Submissions", description = "제출 정보 조회, 피드백, 보상 수령 API")
@RestController
@RequestMapping("/api/v1/submissions")
@RequiredArgsConstructor
public class SubmissionController {
    private final SubmissionService submissionService;

    @Operation(
            summary = "제출 정보 조회",
            description = "특정 유저미션의 제출 정보를 조회합니다.",
            security    = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(name = "userMissionId",
                            description = "유저미션(챌린지) ID",
                            in = ParameterIn.PATH,
                            required = true,
                            schema = @Schema(type = "integer", example = "1001"))
            },
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "조회 성공",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Response.class))),
                    @ApiResponse(responseCode = "404", description = "유저미션 또는 제출 정보를 찾을 수 없음")
            }
    )
    @GetMapping("/{userMissionId}")
    public ResponseEntity<Response<SubmissionResponse>> getSubmissionByMissionId(@PathVariable Long userMissionId) {
        SubmissionResponse submissionResponse = submissionService.getSubmissionByMissionId(userMissionId);
        return ResponseEntity.ok(Response.<SubmissionResponse>builder()
                .data(submissionResponse)
                .message("미션 " + userMissionId + "의 제출 정보를 성공적으로 가져왔습니다.")
                .build());
    }

    @Operation(
            summary = "제출 파일 다운로드",
            description = "특정 유저미션의 제출 파일을 다운로드합니다.",
            security    = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(name = "userMissionId",
                            description = "유저미션 ID",
                            in = ParameterIn.PATH,
                            required = true,
                            schema = @Schema(type = "integer", example = "1001"))
            },
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "파일 다운로드 성공",
                            content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)),
                    @ApiResponse(responseCode = "404", description = "유저미션을 찾을 수 없음"),
                    @ApiResponse(responseCode = "404", description = "제출을 찾을 수 없음"),
                    @ApiResponse(responseCode = "500", description = "파일 경로 검증 실패 또는 IO 오류")
            }
    )
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/{userMissionId}/file")
    public ResponseEntity<Resource> getSubmissionFileByMissionId(@PathVariable Long userMissionId) {
        SubmissionResponse submissionResponse = submissionService.getSubmissionByMissionId(userMissionId);
        if (submissionResponse == null || submissionResponse.getFileName() == null) {
            throw new SubmissionNotFoundException();
        }

        // 둘 모두 이미 검증된 파일명이지만 다시 한 번 검증
        String savedFileName = FileUploadUtils.getSafeFileName(submissionResponse.getFileName());
        String originalFileName = FileUploadUtils.getSafeFileName(submissionResponse.getOriginalFileName());

        String uploadDir = FileUploadUtils.getUploadPath(FileUploadType.MISSION_UPLOADS).toString();

        File file = new File(uploadDir, savedFileName);

        try {
            if (!file.getCanonicalPath().startsWith(new File(uploadDir).getCanonicalPath())) {
                throw new SubMissionInternalException("잘못된 파일 경로입니다.");
            }
        } catch (IOException e) {
            throw new SubMissionInternalException("파일 경로를 확인하는 중 오류가 발생했습니다.");
        }

        String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;

        Resource resource = new FileSystemResource(file);

        if (!resource.exists() || !resource.isReadable()) {
            throw new SubmissionNotFoundException("제출 파일을 찾을 수 없습니다.");
        }

        ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
                .filename(originalFileName, StandardCharsets.UTF_8)
                .build();

        try {
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .contentLength(resource.contentLength())
                    .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                    .body(resource);
        } catch (IOException e) {
            throw new SubmissionNotFoundException("파일 크기를 읽을 수 없습니다.");
        }
    }

    @Operation(
            summary = "제출 피드백 업데이트",
            description = "제출된 미션에 대해 강사가 피드백을 업데이트합니다.",
            security    = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(name = "userMissionId",
                            description = "유저미션 ID",
                            in = ParameterIn.PATH,
                            required = true,
                            schema = @Schema(type = "integer", example = "1001"))
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "피드백 내용",
                    required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SubmissionFeedbackRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "피드백 업데이트 성공",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Response.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
                    @ApiResponse(responseCode = "404", description = "유저미션 정보를 찾을 수 없음"),
                    @ApiResponse(responseCode = "404", description = "제출 정보를 찾을 수 없음"),
            }
    )
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @PatchMapping("/{userMissionId}/feedback")
    public ResponseEntity<Response<Void>> updateSubmissionFeedback(@PathVariable Long userMissionId,
                                                                   @Valid @RequestBody SubmissionFeedbackRequest submissionFeedbackRequest) {

        submissionService.updateSubmissionFeedback(userMissionId, submissionFeedbackRequest);
        return ResponseEntity.ok(Response.<Void>builder()
                .message("제출 피드백이 성공적으로 업데이트되었습니다.")
                .build());
    }

    @Operation(
            summary = "보상 수령",
            description = "유저가 해당 제출 건에 대한 보상(포인트, 경험치) 을 수령합니다.",
            parameters = {
                    @Parameter(name = "userMissionId",
                            description = "유저미션 ID",
                            in = ParameterIn.PATH,
                            required = true,
                            schema = @Schema(type = "integer", example = "1001"))
            },
            security = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "보상 수령 성공",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Response.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "400", description = "이미 보상을 수령한 제출"),
                    @ApiResponse(responseCode = "400", description = "피드백이 완료되지 않은 제출"),
                    @ApiResponse(responseCode = "404", description = "유저를 찾을 수 없음"),
                    @ApiResponse(responseCode = "404", description = "유저미션 정보를 찾을 수 없음"),

            }
    )
    @PostMapping("/{userMissionId}/reward")
    public ResponseEntity<Response<Void>> receiveReward(@PathVariable Long userMissionId,
                                                        @AuthenticationPrincipal UserDetails userDetails) {
        submissionService.receiveReward(userMissionId, userDetails.getUsername());
        return ResponseEntity.ok(Response.<Void>builder()
                .message("보상이 성공적으로 수령되었습니다.")
                .build());
    }
}
