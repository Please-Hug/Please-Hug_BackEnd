package org.example.hugmeexp.domain.mission.entity;

import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Getter
public class MissionRewardPointLog extends MissionLogBaseEntity {
    private Integer prevPoint;
    private Integer nextPoint;

    @Builder
    public MissionRewardPointLog(UserMission userMission, Integer prevPoint, Integer nextPoint, String note) {
        super(userMission, note);
        this.prevPoint = prevPoint;
        this.nextPoint = nextPoint;
    }

    public MissionRewardPointLog() {
        super(null, null);
    }
}
