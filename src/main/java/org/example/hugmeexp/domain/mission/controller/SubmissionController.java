package org.example.hugmeexp.domain.mission.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.hugmeexp.domain.mission.dto.request.SubmissionFeedbackRequest;
import org.example.hugmeexp.domain.mission.dto.response.SubmissionResponse;
import org.example.hugmeexp.domain.mission.enums.FileUploadType;
import org.example.hugmeexp.domain.mission.exception.SubMissionInternalException;
import org.example.hugmeexp.domain.mission.exception.SubmissionNotFoundException;
import org.example.hugmeexp.domain.mission.service.MissionService;
import org.example.hugmeexp.domain.mission.util.FileUploadUtils;
import org.example.hugmeexp.global.common.response.Response;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/v1/submissions")
@RequiredArgsConstructor
public class SubmissionController {
    private final MissionService missionService;


    @GetMapping("/{userMissionId}")
    public ResponseEntity<Response<?>> getSubmissionByMissionId(@PathVariable Long userMissionId) {
        SubmissionResponse submissionResponse = missionService.getSubmissionByMissionId(userMissionId);
        return ResponseEntity.ok(Response.builder()
                .data(submissionResponse)
                .message("미션 " + userMissionId + "의 제출 정보를 성공적으로 가져왔습니다.")
                .build());
    }

    @GetMapping("/{userMissionId}/file")
    public ResponseEntity<Resource> getSubmissionFileByMissionId(@PathVariable Long userMissionId) {
        SubmissionResponse submissionResponse = missionService.getSubmissionByMissionId(userMissionId);
        if (submissionResponse == null || submissionResponse.getFileName() == null) {
            throw new SubmissionNotFoundException();
        }

        // 둘 모두 이미 검증된 파일명이지만 다시 한 번 검증
        String savedFileName = FileUploadUtils.getSafeFileName(submissionResponse.getFileName());
        String originalFileName = FileUploadUtils.getSafeFileName(submissionResponse.getOriginalFileName());

        String uploadDir = FileUploadUtils.getUploadDir(FileUploadType.MISSION_UPLOADS);

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

    @PatchMapping("/{userMissionId}/feedback")
    public ResponseEntity<Response<?>> updateSubmissionFeedback(@PathVariable Long userMissionId,
                                                                @Valid @RequestBody SubmissionFeedbackRequest submissionFeedbackRequest) {

        missionService.updateSubmissionFeedback(userMissionId, submissionFeedbackRequest);
        return ResponseEntity.ok(Response.builder()
                .message("제출 피드백이 성공적으로 업데이트되었습니다.")
                .build());
    }
}
