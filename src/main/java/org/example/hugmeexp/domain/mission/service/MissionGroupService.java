package org.example.hugmeexp.domain.mission.service;

import org.example.hugmeexp.domain.mission.dto.request.MissionGroupRequest;
import org.example.hugmeexp.domain.mission.dto.response.MissionGroupResponse;
import java.util.List;

public interface MissionGroupService {
    List<MissionGroupResponse> getAllMissionGroups();

    MissionGroupResponse createMissionGroup(MissionGroupRequest request);

    MissionGroupResponse getMissionById(Long id);

    MissionGroupResponse updateMissionGroup(Long id, MissionGroupRequest request);

    void deleteMissionGroup(Long id);
}
