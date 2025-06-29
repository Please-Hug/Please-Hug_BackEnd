package org.example.hugmeexp.domain.mission.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.hugmeexp.domain.mission.dto.request.MissionRequest;
import org.example.hugmeexp.domain.mission.service.MissionService;
import org.example.hugmeexp.domain.missionTask.dto.request.MissionTaskRequest;
import org.example.hugmeexp.domain.missionTask.dto.response.MissionTaskResponse;
import org.example.hugmeexp.domain.missionTask.dto.response.UserMissionTaskResponse;
import org.example.hugmeexp.domain.missionTask.service.MissionTaskService;
import org.example.hugmeexp.global.common.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/missions")
@RequiredArgsConstructor
public class MissionController {
    private final MissionService missionService;
    private final MissionTaskService missionTaskService;

    @GetMapping
    public ResponseEntity<Response<?>> getAllMissions() {
        return ResponseEntity.ok(Response.builder()
                .data(missionService.getAllMissions())
                .message("미션 목록을 성공적으로 가져왔습니다.")
                .build());
    }

    @PostMapping
    public ResponseEntity<Response<?>> createMission(@Valid @RequestBody MissionRequest missionRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.builder()
                        .data(missionService.createMission(missionRequest))
                        .message("미션이 성공적으로 생성되었습니다.")
                        .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<?>> getMissionById(@PathVariable Long id) {
        return ResponseEntity.ok(Response.builder()
                .data(missionService.getMissionById(id))
                .message("미션 " + id + "를 가져왔습니다.")
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response<?>> updateMission(@PathVariable Long id, @Valid @RequestBody MissionRequest missionRequest) {
        return ResponseEntity.ok(Response.builder()
                .data(missionService.updateMission(id, missionRequest))
                .message("미션 " + id + "를 업데이트 하였습니다.")
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response<?>> deleteMission(@PathVariable Long id) {
        missionService.deleteMission(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/group")
    public ResponseEntity<Response<?>> changeMissionGroup(@PathVariable Long id, @RequestParam Long missionGroupId) {
        return ResponseEntity.ok(Response.builder()
                .data(missionService.changeMissionGroup(id, missionGroupId))
                .message("미션 " + id + "의 미션 그룹을 변경하였습니다.")
                .build());
    }

    @PostMapping("/{id}/challenges")
    public ResponseEntity<Response<?>> challengeMission(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Response.builder()
                .data(missionService.challengeMission(userDetails.getUsername(), id))
                .message("미션 " + id + "에 도전하였습니다.")
                .build());
    }

    @GetMapping("/{missionId}/tasks")
    public ResponseEntity<Response<List<MissionTaskResponse>>> getAllMissionTasksByMissionId(@PathVariable Long missionId) {
        return ResponseEntity.status(HttpStatus.OK).body(Response.<List<MissionTaskResponse>>builder().
                data(missionTaskService.findByMissionId(missionId))
                .build());
    }

    @GetMapping("/{missionId}/my-tasks")
    public ResponseEntity<Response<List<UserMissionTaskResponse>>> getMyMissionTasks(@PathVariable Long missionId, @AuthenticationPrincipal UserDetails userDetails) {
        List<UserMissionTaskResponse> myTasks = missionTaskService.findUserMissionTasksByUsernameAndMissionId(userDetails.getUsername(), missionId);
        return ResponseEntity.ok(Response.<List<UserMissionTaskResponse>>builder()
                .data(myTasks)
                .message("내 미션 태스크 목록을 성공적으로 가져왔습니다.")
                .build());
    }

    @PostMapping("/{missionId}/tasks")
    public ResponseEntity<Response<Boolean>> addMissionTask(@PathVariable Long missionId, @Valid @RequestBody MissionTaskRequest request) {
        MissionTaskResponse response = missionTaskService.addMissionTask(missionId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Response.<Boolean>builder()
                .data(response != null)
                .message("미션 태스크를 성공적으로 추가했습니다.")
                .build());
    }
}
