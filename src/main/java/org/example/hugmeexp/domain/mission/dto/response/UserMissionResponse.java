package org.example.hugmeexp.domain.mission.dto.response;

import lombok.Builder;
import lombok.Data;
import org.example.hugmeexp.domain.mission.entity.Mission;
import org.example.hugmeexp.domain.mission.enums.UserMissionState;
import org.example.hugmeexp.domain.missionGroup.entity.UserMissionGroup;
import org.example.hugmeexp.domain.user.entity.User;

@Data
@Builder(toBuilder = true)
public class UserMissionResponse {
    private Long id;
    private User user;
    private Mission mission;
    private UserMissionGroup userMissionGroup;
    private UserMissionState progress;
}
