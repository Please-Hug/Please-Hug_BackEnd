package org.example.hugmeexp.domain.mission.repository;

import org.example.hugmeexp.domain.mission.entity.UserMission;
import org.example.hugmeexp.domain.mission.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserMissionSubmissionRepository extends JpaRepository<Submission, Long> {
    boolean existsByUserMission(UserMission userMission);

    Optional<Submission> findByUserMission(UserMission userMission);
}
