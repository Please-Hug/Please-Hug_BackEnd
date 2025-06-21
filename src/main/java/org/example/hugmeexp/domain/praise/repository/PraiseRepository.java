package org.example.hugmeexp.domain.praise.repository;

import org.example.hugmeexp.domain.praise.entity.Praise;
import org.example.hugmeexp.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

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

    /* 칭찬 칭찬 비율(한달동안 받은 칭찬 종류 count) */
    @Query("SELECT p.praiseType, COUNT(p) FROM Praise p "+
            "WHERE p.receiver.id = :userId "+
            "AND p.createdAt BETWEEN :startDateTime AND :endDateTime "+
            "GROUP BY p.praiseType")
    List<Object[]> countPraiseTypeByUserInMonth(Long userId, LocalDateTime startDateTime, LocalDateTime endDateTime);

    /* 최근 칭찬 보낸 유저 조회 */
    @Query("SELECT p FROM Praise p "+
            "WHERE p.receiver.id = :userId "+
            "AND p.createdAt IN (" +
            "   SELECT MAX(p2.createdAt) FROM Praise p2 " +
            "   WHERE p2.receiver.id = :userId " +
            "   GROUP BY p2.sender.id" +
            ") " +
            "ORDER BY p.createdAt DESC")
    List<Praise> findLatestPraisePerSender(Long userId);
}
