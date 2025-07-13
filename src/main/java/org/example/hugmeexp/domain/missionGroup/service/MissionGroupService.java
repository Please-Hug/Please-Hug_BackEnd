package org.example.hugmeexp.domain.missionGroup.service;
import org.example.hugmeexp.domain.mission.dto.response.UserMissionResponse;
import org.example.hugmeexp.domain.missionGroup.dto.request.MissionGroupRequest;
import org.example.hugmeexp.domain.missionGroup.dto.response.MissionGroupResponse;
import org.example.hugmeexp.domain.missionGroup.dto.response.UserMissionGroupResponse;
import org.example.hugmeexp.domain.user.dto.response.UserProfileResponse;

import java.util.List;

public interface MissionGroupService {
    List<MissionGroupResponse> getAllMissionGroups();

    MissionGroupResponse createMissionGroup(MissionGroupRequest request, String username);

    List<MissionGroupResponse> getMissionGroupById(Long id);

    MissionGroupResponse updateMissionGroup(Long id, MissionGroupRequest request);

    void deleteMissionGroup(Long id);

    void addUserToMissionGroup(String username, Long missionGroupId);

    void removeUserFromMissionGroup(String username, Long missionGroupId);

    List<UserMissionResponse> findUserMissionByUsernameAndMissionGroup(String username, Long missionGroupId);

    List<UserMissionGroupResponse> getMyMissionGroups(String username);

    List<UserProfileResponse> getUsersInMissionGroup(Long missionGroupId);
}
