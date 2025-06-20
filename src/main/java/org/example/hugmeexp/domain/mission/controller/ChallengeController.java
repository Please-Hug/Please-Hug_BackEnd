package org.example.hugmeexp.domain.mission.controller;

import lombok.RequiredArgsConstructor;
import org.example.hugmeexp.domain.mission.enums.UserMissionState;
import org.example.hugmeexp.domain.mission.service.MissionService;
import org.example.hugmeexp.global.common.response.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
