package org.example.hugmeexp.domain.mission.service;

import aj.org.objectweb.asm.commons.Remapper;
import org.example.hugmeexp.domain.mission.dto.MissionGroupRequest;
import org.example.hugmeexp.domain.mission.dto.MissionGroupResponse;
import org.example.hugmeexp.domain.mission.dto.MissionGroupUpdateRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

public interface MissionGroupService {
    List<MissionGroupResponse> getAllMissionGroups();

    MissionGroupResponse createMissionGroup(MissionGroupRequest request);

    MissionGroupResponse getMissionById(Long id);

    MissionGroupResponse updateMissionGroup(MissionGroupUpdateRequest request);

    void deleteMissionGroup(Long id);
}
