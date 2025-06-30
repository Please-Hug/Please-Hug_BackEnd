package org.example.hugmeexp.domain.mission.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.hugmeexp.domain.mission.dto.request.SubmissionUploadRequest;
import org.example.hugmeexp.domain.mission.dto.response.UserMissionResponse;
import org.example.hugmeexp.domain.mission.enums.UserMissionState;
import org.example.hugmeexp.domain.mission.service.MissionService;
import org.example.hugmeexp.global.common.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Challenges", description = "챌린지 조회,상태변경,제출 API")
@RestController
@RequestMapping("/api/v1/challenges")
@RequiredArgsConstructor
public class ChallengeController {
    private final MissionService missionService;

    @Operation(
            summary = "챌린지(유저미션) 상태 업데이트",
            description = "특정 챌린지(유저미션)의 진행 상태를 변경합니다.",
            security    = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(name = "challengeId",
                            description = "챌린지 ID (UserMission PK)",
                            in = ParameterIn.PATH,
                            required = true,
                            schema = @Schema(type = "integer", example = "123")),
                    @Parameter(name = "newProgress",
                            description = "변경할 상태 (enum UserMissionState)",
                            in = ParameterIn.QUERY,
                            required = true,
                            schema = @Schema(implementation = UserMissionState.class))
            },
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "상태 업데이트 성공",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Response.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "404", description = "챌린지(유저미션) 없음")
            }
    )
    @PatchMapping("/{challengeId}")
    public ResponseEntity<Response<Void>> updateChallengeState(@PathVariable Long challengeId, @RequestParam UserMissionState newProgress) {
        missionService.changeUserMissionState(challengeId, newProgress);
        return ResponseEntity.ok(Response.<Void>builder()
                .message("챌린지 상태가 성공적으로 업데이트되었습니다.")
                .build());
    }

    @Operation(
            summary = "강사 - 모든 챌린지 조회",
            description = "로그인된 강사가 배정된 모든 유저미션을 조회합니다.",
            security = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "조회 성공",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Response.class)))
            }
    )
    @GetMapping()
    public ResponseEntity<Response<List<UserMissionResponse>>> getAllChallenges(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(Response.<List<UserMissionResponse>>builder()
                .data(missionService.getAllUserMissionsByTeacher(userDetails.getUsername()))
                .message("모든 챌린지를 성공적으로 가져왔습니다.")
                .build());
    }

    @Operation(
            summary = "챌린지 조회",
            description = "주어진 챌린지 ID의 유저미션을 조회합니다.",
            security    = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(name = "challengeId",
                            description = "챌린지 ID (UserMission PK)",
                            in = ParameterIn.PATH,
                            required = true,
                            schema = @Schema(type = "integer", example = "123"))
            },
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "조회 성공",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Response.class))),
                    @ApiResponse(responseCode = "404", description = "챌린지(유저미션) 없음")
            }
    )
    @GetMapping("/{challengeId}")
    public ResponseEntity<Response<UserMissionResponse>> getChallengeById(@PathVariable Long challengeId) {
        return ResponseEntity.ok(Response.<UserMissionResponse>builder()
                .data(missionService.getUserMissionByChallengeId(challengeId))
                .message("챌린지 " + challengeId + "를 성공적으로 가져왔습니다.")
                .build());
    }

    @Operation(
            summary = "챌린지 결과물 제출",
            description = "피드백과 결과물을 함께 업로드하여 챌린지를 제출합니다.",
            security    = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(name = "challengeId",
                            description = "챌린지 ID (UserMission PK)",
                            in = ParameterIn.PATH,
                            required = true,
                            schema = @Schema(type = "integer", example = "123"))
            },
            requestBody = @RequestBody(
                    description = "제출 폼 데이터 (JSON 필드 + 파일)",
                    required = true,
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(implementation = SubmissionUploadRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "201",
                            description = "제출 성공",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Response.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 제출 데이터"),
                    @ApiResponse(responseCode = "404", description = "챌린지(유저미션) 없음"),
                    @ApiResponse(responseCode = "409", description = "이미 제출된 챌린지"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    @PostMapping("/{challengeId}/submissions")
    public ResponseEntity<Response<Void>> submitChallenge(@PathVariable Long challengeId,
                                                          @Valid @ModelAttribute SubmissionUploadRequest submissionUploadRequest,
                                                          @RequestParam("file") MultipartFile file) {
        missionService.submitChallenge(challengeId, submissionUploadRequest, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(Response.<Void>builder()
                .message("챌린지 제출이 성공적으로 완료되었습니다.")
                .build());
    }

}
