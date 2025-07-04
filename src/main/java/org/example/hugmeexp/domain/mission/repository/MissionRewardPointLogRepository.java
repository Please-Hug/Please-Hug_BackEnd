package org.example.hugmeexp.domain.mission.repository;

import org.example.hugmeexp.domain.mission.entity.MissionRewardExpLog;
import org.example.hugmeexp.domain.mission.entity.MissionRewardPointLog;
import org.example.hugmeexp.domain.mission.entity.UserMissionStateLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface MissionRewardPointLogRepository extends JpaRepository<MissionRewardPointLog, Long> {
    @Query("SELECT m FROM MissionRewardPointLog m WHERE m.userMission.user.id = :userId AND m.createdAt BETWEEN :startDateTime AND :endDateTime")
    List<MissionRewardPointLog> findByUserIdAndCreatedAtBetween(Long userId, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
