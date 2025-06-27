package org.example.hugmeexp.domain.mission.repository;

import org.example.hugmeexp.domain.mission.entity.UserMissionStateLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserMissionStateLogRepository extends JpaRepository<UserMissionStateLog, Long> {
    @Query("SELECT m FROM UserMissionStateLog m WHERE m.userMission.user.id = :userId AND m.createdAt BETWEEN :startDateTime AND :endDateTime")
    List<UserMissionStateLog> findByUserIdAndCreatedAtBetween(Long userId, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
