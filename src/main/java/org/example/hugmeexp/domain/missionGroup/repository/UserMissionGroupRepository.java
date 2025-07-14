package org.example.hugmeexp.domain.missionGroup.repository;

import org.example.hugmeexp.domain.missionGroup.entity.MissionGroup;
import org.example.hugmeexp.domain.missionGroup.entity.UserMissionGroup;
import org.example.hugmeexp.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserMissionGroupRepository extends JpaRepository<UserMissionGroup, Long> {
    Boolean existsByUserAndMissionGroup(User user, MissionGroup missionGroup);
    Optional<UserMissionGroup> findByUserAndMissionGroup(User user, MissionGroup missionGroup);

    List<UserMissionGroup> findByUserId(Long userId);

    List<UserMissionGroup> findAllByMissionGroup(MissionGroup missionGroup);

    @Query("SELECT user FROM UserMissionGroup umg JOIN umg.user user WHERE umg.missionGroup = :missionGroup")
    List<User> findUsersByMissionGroup(MissionGroup missionGroup);

    @Query("SELECT umg FROM UserMissionGroup umg JOIN FETCH umg.user JOIN FETCH umg.missionGroup mg JOIN FETCH mg.teacher WHERE umg.user.id = :id")
    List<UserMissionGroup> findByUserIdWithTeacher(Long id);
}
