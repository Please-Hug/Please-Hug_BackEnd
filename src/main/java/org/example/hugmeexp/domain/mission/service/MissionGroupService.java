package org.example.hugmeexp.domain.mission.service;

import org.example.hugmeexp.domain.mission.dto.MissionGroupRequest;
import org.example.hugmeexp.domain.mission.dto.MissionGroupResponse;
import org.example.hugmeexp.domain.mission.dto.MissionGroupUpdateRequest;
import java.util.List;

public interface MissionGroupService {
    List<MissionGroupResponse> getAllMissionGroups();

    MissionGroupResponse createMissionGroup(MissionGroupRequest request);

    MissionGroupResponse getMissionById(Long id);

    MissionGroupResponse updateMissionGroup(MissionGroupUpdateRequest request);

    void deleteMissionGroup(Long id);
}
