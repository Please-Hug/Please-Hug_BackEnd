package org.example.hugmeexp.domain.mission.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.example.hugmeexp.domain.mission.enums.UserMissionState;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class UserMissionStateLogResponse {
    private Long id;
    private UserMissionResponse userMission;
    private UserMissionState prevState;
    private UserMissionState nextState;
    private String createdAt;
    private String modifiedAt;
}
