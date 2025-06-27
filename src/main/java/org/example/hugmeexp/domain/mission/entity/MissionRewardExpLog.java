package org.example.hugmeexp.domain.mission.entity;

import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Getter
public class MissionRewardExpLog extends MissionLogBaseEntity {
    private Integer prevExp;
    private Integer nextExp;

    @Builder
    public MissionRewardExpLog(UserMission userMission, Integer prevExp, Integer nextExp, String note) {
        super(userMission, note);
        this.prevExp = prevExp;
        this.nextExp = nextExp;
    }

    public MissionRewardExpLog() {
        super(null, null);
    }
}
