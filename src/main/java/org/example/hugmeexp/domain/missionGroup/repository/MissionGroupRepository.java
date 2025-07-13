package org.example.hugmeexp.domain.missionGroup.repository;

import org.example.hugmeexp.domain.missionGroup.entity.MissionGroup;
import org.example.hugmeexp.domain.user.mapper.ProfileImageMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MissionGroupRepository extends JpaRepository<MissionGroup, Long> {
    @Query("SELECT mg FROM MissionGroup mg JOIN FETCH mg.teacher")
    List<MissionGroup> findAllWithTeacher();

    @Query("SELECT mg FROM MissionGroup mg JOIN FETCH mg.teacher WHERE mg.id = :id")
    Optional<MissionGroup> findByIdWithTeacher(Long id);
}
