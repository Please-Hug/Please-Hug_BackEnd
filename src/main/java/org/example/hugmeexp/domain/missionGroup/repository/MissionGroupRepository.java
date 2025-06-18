package org.example.hugmeexp.domain.missionGroup.repository;

import org.example.hugmeexp.domain.missionGroup.entity.MissionGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MissionGroupRepository extends JpaRepository<MissionGroup, Long> {

}
