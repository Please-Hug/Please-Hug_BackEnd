package org.example.hugmeexp.domain.missionTask.repository;

import org.example.hugmeexp.domain.missionTask.entity.MissionTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MissionTaskRepository extends JpaRepository<MissionTask, Long> {
    List<MissionTask> findByMissionId(Long missionId);
}
