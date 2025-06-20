package org.example.hugmeexp.domain.mission.dto.response;

import lombok.Builder;
import lombok.Data;
import org.example.hugmeexp.domain.mission.enums.UserMissionState;
import org.example.hugmeexp.domain.missionGroup.dto.response.UserMissionGroupResponse;
import org.example.hugmeexp.domain.user.dto.response.UserSimpleResponse;

@Data
@Builder(toBuilder = true)
public class UserMissionResponse {
    private Long id;
    private UserSimpleResponse user;
    private MissionResponse mission;
    private UserMissionGroupResponse userMissionGroup;
    private UserMissionState progress;
}
