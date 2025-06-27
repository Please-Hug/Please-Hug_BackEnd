package org.example.hugmeexp.domain.mission.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.hugmeexp.domain.mission.enums.Difficulty;
import org.example.hugmeexp.domain.missionGroup.entity.MissionGroup;
import org.example.hugmeexp.global.entity.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class Mission extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_group_id", nullable = false)
    private MissionGroup missionGroup;

    @Column(name = "name", nullable = false, length = 127)
    private String name;

    @Column(name = "description", nullable = false, length = 1023)
    private String description;

    @Column(name = "difficulty", nullable = false)
    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    @Column(name = "reward_point", nullable = false)
    private int rewardPoint;

    @Column(name = "reward_exp", nullable = false)
    private int rewardExp;

    @Column(name = "mission_order", nullable = false)
    private int order; // 순서, missionGroup 내에서의 순서

    @Column(name = "mission_line", nullable = false)
    private int line;
}
