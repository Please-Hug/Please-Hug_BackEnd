package org.example.hugmeexp.domain.missionGroup.service;
import org.example.hugmeexp.domain.mission.dto.response.UserMissionResponse;
import org.example.hugmeexp.domain.missionGroup.dto.request.MissionGroupRequest;
import org.example.hugmeexp.domain.missionGroup.dto.response.MissionGroupResponse;
import org.example.hugmeexp.domain.missionGroup.dto.response.UserMissionGroupResponse;

import java.util.List;

public interface MissionGroupService {
    List<MissionGroupResponse> getAllMissionGroups();

    MissionGroupResponse createMissionGroup(MissionGroupRequest request);

    MissionGroupResponse getMissionById(Long id);

    MissionGroupResponse updateMissionGroup(Long id, MissionGroupRequest request);

    void deleteMissionGroup(Long id);

    void addUserToMissionGroup(Long userId, Long missionGroupId);

    void removeUserFromMissionGroup(Long userId, Long missionGroupId);

    List<UserMissionResponse> findUserMissionByUsernameAndMissionGroup(String username, Long missionGroupId);

    List<UserMissionGroupResponse> getMyMissionGroups(String username);
}
