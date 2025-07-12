package org.example.hugmeexp.domain.mission.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.hugmeexp.domain.mission.enums.UserMissionState;
import org.example.hugmeexp.domain.missionGroup.entity.UserMissionGroup;
import org.example.hugmeexp.domain.user.entity.User;
import org.example.hugmeexp.global.entity.BaseEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "mission_id", "user_mission_group_id"})})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserMission extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Mission mission;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserMissionGroup userMissionGroup;

    @Setter
    @Enumerated(EnumType.STRING)
    private UserMissionState progress;

    @OneToOne(mappedBy = "userMission", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Submission submissions;

    @OneToMany(mappedBy = "userMission", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MissionRewardExpLog> missionRewardExpLogs = new ArrayList<>();

    @OneToMany(mappedBy = "userMission", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MissionRewardPointLog> missionRewardPointLogs = new ArrayList<>();

    @OneToMany(mappedBy = "userMission", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<UserMissionStateLog> userMissionStateLogs = new ArrayList<>();
}
