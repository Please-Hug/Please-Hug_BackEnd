package org.example.hugmeexp.domain.missionGroup.dto.response;

import lombok.Builder;
import lombok.Data;

import org.example.hugmeexp.domain.user.dto.response.UserSimpleResponse;
@Data
@Builder(toBuilder = true)
public class UserMissionGroupResponse {
    private Long id;
    private UserSimpleResponse user;
    private MissionGroupResponse missionGroup;
}
