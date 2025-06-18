package org.example.hugmeexp.domain.missionGroup.service;

import org.example.hugmeexp.domain.missionGroup.dto.request.MissionGroupRequest;
import org.example.hugmeexp.domain.missionGroup.dto.response.MissionGroupResponse;
import java.util.List;

public interface MissionGroupService {
    List<MissionGroupResponse> getAllMissionGroups();

    MissionGroupResponse createMissionGroup(MissionGroupRequest request);

    MissionGroupResponse getMissionById(Long id);

    MissionGroupResponse updateMissionGroup(Long id, MissionGroupRequest request);

    void deleteMissionGroup(Long id);
}
