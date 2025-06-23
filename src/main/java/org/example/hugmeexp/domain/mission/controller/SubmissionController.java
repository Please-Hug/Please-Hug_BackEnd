package org.example.hugmeexp.domain.mission.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.hugmeexp.domain.mission.dto.request.SubmissionFeedbackRequest;
import org.example.hugmeexp.domain.mission.dto.response.SubmissionResponse;
import org.example.hugmeexp.domain.mission.exception.SubMissionInternalException;
import org.example.hugmeexp.domain.mission.exception.SubmissionNotFoundException;
import org.example.hugmeexp.domain.mission.service.MissionService;
import org.example.hugmeexp.global.common.response.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/v1/submissions")
@RequiredArgsConstructor
public class SubmissionController {
    private final MissionService missionService;

    @Value("${file.submission-upload-dir}")
    private String uploadDir;

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
        String savedFileName = getSafeFileName(submissionResponse.getFileName());
        String originalFileName = getSafeFileName(submissionResponse.getOriginalFileName()).replaceAll("[\"'/\\\\]", "_");
        File file = new File(uploadDir, savedFileName);
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

    private static String getSafeFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            throw new SubMissionInternalException("파일 이름이 비어 있거나 null입니다.");
        }

        return StringUtils.getFilename(StringUtils.cleanPath(fileName));
    }
}
