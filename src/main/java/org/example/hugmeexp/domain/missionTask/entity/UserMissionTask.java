package org.example.hugmeexp.domain.missionTask.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.hugmeexp.domain.mission.entity.UserMission;
import org.example.hugmeexp.domain.missionTask.enums.TaskState;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserMissionTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_mission_id", nullable = false)
    private UserMission userMission;

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "mission_task_id", nullable = false)
    private MissionTask missionTask;

    @Setter
    @Column(name = "state", nullable = false)
    @Enumerated(EnumType.STRING)
    private TaskState state;
}
