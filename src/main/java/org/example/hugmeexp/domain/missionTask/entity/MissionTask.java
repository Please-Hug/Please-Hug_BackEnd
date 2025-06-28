package org.example.hugmeexp.domain.missionTask.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.hugmeexp.domain.mission.entity.Mission;
import org.example.hugmeexp.global.entity.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class MissionTask extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    @Setter
    private Mission mission;

    @Setter
    @Column(name = "name", nullable = false, length = 127)
    private String name;

    @Setter
    @Column(name = "score", nullable = false)
    private int score; // 공수
}
