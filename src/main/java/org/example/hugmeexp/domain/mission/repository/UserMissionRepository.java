package org.example.hugmeexp.domain.mission.repository;

import org.example.hugmeexp.domain.mission.entity.UserMission;
import org.example.hugmeexp.domain.missionGroup.entity.MissionGroup;
import org.example.hugmeexp.domain.missionGroup.entity.UserMissionGroup;
import org.example.hugmeexp.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserMissionRepository extends JpaRepository<UserMission, Long> {

    List<UserMission> findByUserAndUserMissionGroup(User user, UserMissionGroup userMissionGroup);
}
