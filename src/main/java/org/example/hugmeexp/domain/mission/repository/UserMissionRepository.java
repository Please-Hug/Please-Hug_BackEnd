package org.example.hugmeexp.domain.mission.repository;

import org.example.hugmeexp.domain.mission.entity.Mission;
import org.example.hugmeexp.domain.mission.entity.UserMission;
import org.example.hugmeexp.domain.missionGroup.entity.UserMissionGroup;
import org.example.hugmeexp.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserMissionRepository extends JpaRepository<UserMission, Long> {

    @Query("SELECT um FROM UserMission um JOIN FETCH um.user WHERE um.id = :id")
    Optional<UserMission> findByIdWithUser(Long id);

    List<UserMission> findByUserAndUserMissionGroup(User user, UserMissionGroup userMissionGroup);

    boolean existsUserMissionByUserAndMission(User user, Mission mission);

    Optional<UserMission> findByUserAndMission(User user, Mission mission);

    List<UserMission> findAllByMission_MissionGroup_Teacher(User missionMissionGroupTeacher);
}
