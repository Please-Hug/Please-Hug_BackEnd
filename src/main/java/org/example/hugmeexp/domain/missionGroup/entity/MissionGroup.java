package org.example.hugmeexp.domain.missionGroup.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.hugmeexp.domain.mission.entity.Mission;
import org.example.hugmeexp.global.entity.BaseEntity;
import org.example.hugmeexp.domain.user.entity.User;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class MissionGroup extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "teacher_id")
    private User teacher;
    @Column(nullable = false, length = 127)
    private String name;

    @OneToMany(mappedBy = "missionGroup", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<UserMissionGroup> userMissionGroups = new ArrayList<>();

    @OneToMany(mappedBy = "missionGroup", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Mission> missions = new ArrayList<>();
}
