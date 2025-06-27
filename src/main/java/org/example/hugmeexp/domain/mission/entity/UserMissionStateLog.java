package org.example.hugmeexp.domain.mission.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import org.example.hugmeexp.domain.mission.enums.UserMissionState;

@Entity
@Getter
public class UserMissionStateLog extends MissionLogBaseEntity {
    @Enumerated(EnumType.STRING)
    private UserMissionState prevState;
    @Enumerated(EnumType.STRING)
    private UserMissionState nextState;

    @Builder
    public UserMissionStateLog(UserMission userMission, UserMissionState prevState, UserMissionState nextState, String note) {
        super(userMission, note);
        this.prevState = prevState;
        this.nextState = nextState;
    }

    public UserMissionStateLog() {
        super(null, null);
    }
}
