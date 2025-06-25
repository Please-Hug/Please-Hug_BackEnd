package org.example.hugmeexp.domain.mission.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.hugmeexp.global.entity.BaseEntity;

@Getter
@MappedSuperclass
public class MissionLogBaseEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserMission userMission;

    @Column()
    private String note;

    public MissionLogBaseEntity(UserMission userMission, String note) {
        super();
        this.userMission = userMission;
        this.note = note;
    }
}
