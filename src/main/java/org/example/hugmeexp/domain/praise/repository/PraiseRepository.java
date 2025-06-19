package org.example.hugmeexp.domain.praise.repository;

import org.example.hugmeexp.domain.praise.dto.PraiseResponseDTO;
import org.example.hugmeexp.domain.praise.entity.Praise;
import org.example.hugmeexp.global.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

@Repository
public interface PraiseRepository extends JpaRepository<Praise, Long> {

    /* 칭찬 생성 */
    List<Praise> findByCreatedAtBetween(LocalDateTime createdAtAfter, LocalDateTime createdAtBefore);

    /* 칭찬 날짜 + ME*/
    @Query("SELECT p FROM Praise p " +
            "WHERE p.createdAt BETWEEN :startDateTime AND :endDateTime " +
            "AND (p.sender = :currentUser OR p.receiver = :currentUser)")
    List<Praise> findByDateRangeAndUser(LocalDateTime startDateTime, LocalDateTime endDateTime, User currentUser);

    /* 칭찬 날짜 + ME + keyword */
    @Query("SELECT p FROM Praise p " +
            "WHERE p.createdAt BETWEEN :startDateTime AND :endDateTime " +
            "AND (p.sender = :currentUser OR p.receiver = :currentUser) " +
            "AND (p.sender.name LIKE %:keyword% OR p.receiver.name LIKE %:keyword%)")
    List<Praise> findByDateAndUserAndKeyword(LocalDateTime startDateTime, LocalDateTime endDateTime, User currentUser, String keyword);

    /* 칭찬 날짜 + keyword*/
    @Query("SELECT p FROM Praise p " +
            "WHERE p.createdAt BETWEEN :startDateTime AND :endDateTime " +
            "AND (p.sender.name LIKE %:keyword% OR p.receiver.name LIKE %:keyword%)")
    List<Praise> findByDateAndKeyword(LocalDateTime startDateTime, LocalDateTime endDateTime, String keyword);

}
