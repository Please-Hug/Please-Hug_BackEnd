package org.example.hugmeexp.domain.missionTask.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.hugmeexp.domain.missionTask.dto.request.MissionTaskRequest;
import org.example.hugmeexp.domain.missionTask.enums.TaskState;
import org.example.hugmeexp.domain.missionTask.service.MissionTaskService;
import org.example.hugmeexp.global.common.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/mission-tasks")
@RequiredArgsConstructor
public class MissionTaskController {
    private final MissionTaskService missionTaskService;

    @DeleteMapping("/{missionTaskId}")
    public ResponseEntity<Void> deleteMissionTask(@PathVariable Long missionTaskId) {
        missionTaskService.deleteMissionTask(missionTaskId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{missionTaskId}")
    public ResponseEntity<Response<Boolean>> updateMissionTask(@PathVariable Long missionTaskId, @Valid @RequestBody MissionTaskRequest request) {
        missionTaskService.updateMissionTask(missionTaskId, request);
        return ResponseEntity.status(HttpStatus.OK).body(Response.<Boolean>builder()
                .data(true)
                .message("미션 태스크를 성공적으로 업데이트했습니다.")
                .build());
    }

    @PostMapping("/{missionTaskId}/{taskState}")
    public ResponseEntity<Response<Boolean>> updateMissionTaskState(@PathVariable Long missionTaskId,
                                                                    @PathVariable TaskState taskState,
                                                                    @AuthenticationPrincipal UserDetails userDetails) {
        missionTaskService.changeUserMissionTaskState(userDetails.getUsername(), missionTaskId, taskState);
        return ResponseEntity.status(HttpStatus.OK).body(Response.<Boolean>builder()
                .data(true)
                .message("미션 태스크 상태를 성공적으로 업데이트했습니다.")
                .build());
    }
}
