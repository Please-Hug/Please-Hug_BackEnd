package org.example.hugmeexp.domain.praise.repository;

import org.example.hugmeexp.domain.praise.entity.Praise;
import org.example.hugmeexp.global.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PraiseRepository extends JpaRepository<Praise, Long> {
    List<Praise> findByCreatedAtBetween(LocalDateTime createdAtAfter, LocalDateTime createdAtBefore);

    @Query("SELECT p FROM Praise p " +
            "WHERE p.createdAt BETWEEN :startDateTime AND :endDateTime " +
            "AND (p.sender = :currentUser OR p.receiver = :currentUser)")
    List<Praise> findByDateRangeAndUser(LocalDateTime startDateTime, LocalDateTime endDateTime, User currentUser);

}
