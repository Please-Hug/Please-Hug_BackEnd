package org.example.hugmeexp.domain.mission.repository;

import org.example.hugmeexp.domain.mission.entity.MissionRewardExpLog;
import org.example.hugmeexp.domain.mission.entity.MissionRewardPointLog;
import org.example.hugmeexp.domain.mission.entity.UserMissionStateLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface MissionRewardPointLogRepository extends JpaRepository<MissionRewardPointLog, Long> {
    @Query("SELECT m FROM MissionRewardExpLog m WHERE m.userMission.user.id = :userId AND m.createdAt BETWEEN :startDateTime AND :endDateTime")
    List<MissionRewardExpLog> findByUserIdAndCreatedAtBetween(Long userId, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
