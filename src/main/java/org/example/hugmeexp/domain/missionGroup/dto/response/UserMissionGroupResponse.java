package org.example.hugmeexp.domain.missionGroup.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import org.example.hugmeexp.domain.user.dto.response.UserProfileResponse;
@Data
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserMissionGroupResponse {
    private Long id;
    private UserProfileResponse user;
    private MissionGroupResponse missionGroup;
}
