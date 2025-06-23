package org.example.hugmeexp.domain.missionGroup.repository;

import org.example.hugmeexp.domain.missionGroup.entity.MissionGroup;
import org.example.hugmeexp.domain.missionGroup.entity.UserMissionGroup;
import org.example.hugmeexp.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserMissionGroupRepository extends JpaRepository<UserMissionGroup, Long> {
    Boolean existsByUserAndMissionGroup(User user, MissionGroup missionGroup);
    Optional<UserMissionGroup> findByUserAndMissionGroup(User user, MissionGroup missionGroup);
}
