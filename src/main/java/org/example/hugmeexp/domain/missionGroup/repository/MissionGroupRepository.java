package org.example.hugmeexp.domain.missionGroup.repository;

import org.example.hugmeexp.domain.missionGroup.entity.MissionGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MissionGroupRepository extends JpaRepository<MissionGroup, Long> {
    @Query("SELECT mg FROM MissionGroup mg JOIN FETCH mg.teacher")
    List<MissionGroup> findAllWithTeacher();
}
