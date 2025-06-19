package org.example.hugmeexp.domain.missionGroup.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.hugmeexp.domain.mission.service.MissionService;
import org.example.hugmeexp.domain.missionGroup.dto.request.MissionGroupRequest;
import org.example.hugmeexp.domain.missionGroup.dto.response.MissionGroupResponse;
import org.example.hugmeexp.domain.missionGroup.service.MissionGroupService;
import org.example.hugmeexp.global.common.response.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mission-groups")
@RequiredArgsConstructor
public class MissionGroupController {
    private final MissionGroupService missionGroupService;
    private final MissionService missionService;

    @GetMapping
    public ResponseEntity<Response<?>> getMissionGroups() {
        List<MissionGroupResponse> missionGroups = missionGroupService.getAllMissionGroups();
        return ResponseEntity.ok().body(Response.builder().data(missionGroups).message("미션 그룹 목록을 성공적으로 가져왔습니다.").build());
    }

    @PostMapping
    public ResponseEntity<Response<?>> createMissionGroup(@Valid @RequestBody MissionGroupRequest request) {
        return ResponseEntity.status(201).body(Response.builder().data(missionGroupService.createMissionGroup(request)).message("미션 그룹을 생성하였습니다.").build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<?>> getMissionGroup(@PathVariable Long id) {
        return ResponseEntity.ok().body(Response.builder().data(missionGroupService.getMissionById(id)).message("미션그룹 " + id + "를 가져왔습니다.").build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response<?>> updateMissionGroup(@PathVariable Long id, @Valid @RequestBody MissionGroupRequest request) {
        return ResponseEntity.ok().body(Response.builder().data(missionGroupService.updateMissionGroup(id, request)).message("미션그룹 " + id + "를 업데이트 하였습니다.").build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response<?>> deleteMissionGroup(@PathVariable Long id) {
        missionGroupService.deleteMissionGroup(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/missions")
    public ResponseEntity<Response<?>> getMissionsByMissionGroupId(@PathVariable Long id) {
        return ResponseEntity.ok().body(Response.builder()
                .data(missionService.getMissionsByMissionGroupId(id))
                .message("미션 그룹 " + id + "의 미션 목록을 가져왔습니다.")
                .build());
    }
}
