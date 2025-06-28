package org.example.hugmeexp.domain.missionTask.repository;

import org.example.hugmeexp.domain.mission.entity.UserMission;
import org.example.hugmeexp.domain.missionTask.entity.MissionTask;
import org.example.hugmeexp.domain.missionTask.entity.UserMissionTask;
import org.example.hugmeexp.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserMissionTaskRepository extends JpaRepository<UserMissionTask, Long> {
    Optional<UserMissionTask> findByUserMission_UserAndMissionTask (User user, MissionTask missionTask);

    List<UserMissionTask> findByUserMission(UserMission userMission);

}
