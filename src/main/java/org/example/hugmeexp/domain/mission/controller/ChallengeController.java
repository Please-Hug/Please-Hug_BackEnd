package org.example.hugmeexp.domain.mission.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.hugmeexp.domain.mission.dto.request.SubmissionUploadRequest;
import org.example.hugmeexp.domain.mission.enums.UserMissionState;
import org.example.hugmeexp.domain.mission.service.MissionService;
import org.example.hugmeexp.global.common.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/challenges")
@RequiredArgsConstructor
public class ChallengeController {
    private final MissionService missionService;

    @PatchMapping("/{challengeId}")
    public ResponseEntity<Response<?>> updateChallengeState(@PathVariable Long challengeId, @RequestParam UserMissionState newProgress) {
        missionService.changeUserMissionState(challengeId, newProgress);
        return ResponseEntity.ok(Response.builder()
                .message("챌린지 상태가 성공적으로 업데이트되었습니다.")
                .build());
    }

    @PostMapping("/{challengeId}/submissions")
    public ResponseEntity<Response<?>> submitChallenge(@PathVariable Long challengeId,
                                                       @Valid @ModelAttribute SubmissionUploadRequest submissionUploadRequest,
                                                       @RequestParam("file") MultipartFile file) {
        missionService.submitChallenge(challengeId, submissionUploadRequest, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(Response.builder()
                .message("챌린지 제출이 성공적으로 완료되었습니다.")
                .build());
    }

}
