package org.example.hugmeexp.domain.mission.controller;

import lombok.RequiredArgsConstructor;
import org.example.hugmeexp.domain.mission.dto.MissionGroupRequest;
import org.example.hugmeexp.domain.mission.dto.MissionGroupResponse;
import org.example.hugmeexp.domain.mission.dto.MissionGroupUpdateRequest;
import org.example.hugmeexp.domain.mission.service.MissionGroupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mission-groups")
@RequiredArgsConstructor
public class MissionGroupController {
    private final MissionGroupService missionGroupService;

    @GetMapping
    public ResponseEntity<List<MissionGroupResponse>> getMissionGroups() {
        List<MissionGroupResponse> missionGroups = missionGroupService.getAllMissionGroups();
        return ResponseEntity.ok(missionGroups);
    }

    @PostMapping
    public ResponseEntity<MissionGroupResponse> createMissionGroup(@RequestBody MissionGroupRequest request) {
        MissionGroupResponse response = missionGroupService.createMissionGroup(request);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MissionGroupResponse> getMissionGroup(@PathVariable Long id) {
        return ResponseEntity.ok().body(missionGroupService.getMissionById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MissionGroupResponse> updateMissionGroup(@PathVariable Long id, @RequestBody MissionGroupUpdateRequest request) {
        return ResponseEntity.ok().body(missionGroupService.updateMissionGroup(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMissionGroup(@PathVariable Long id) {
        missionGroupService.deleteMissionGroup(id);
        return ResponseEntity.noContent().build();
    }
}
