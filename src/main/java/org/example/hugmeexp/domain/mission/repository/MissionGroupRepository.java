package org.example.hugmeexp.domain.mission.repository;

import org.example.hugmeexp.domain.mission.entity.MissionGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MissionGroupRepository extends JpaRepository<MissionGroup, Long> {

}
