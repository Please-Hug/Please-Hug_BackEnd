package org.example.hugmeexp.domain.mission.repository;

import org.example.hugmeexp.domain.mission.entity.Mission;
import org.example.hugmeexp.domain.missionGroup.entity.MissionGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MissionRepository extends JpaRepository<Mission, Long> {
    List<Mission> findMissionByMissionGroup(MissionGroup missionGroup);
}
