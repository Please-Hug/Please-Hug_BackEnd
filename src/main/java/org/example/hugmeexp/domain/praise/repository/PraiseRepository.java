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

    /* 기존 날짜 조회 */
    List<Praise> findByCreatedAtBetween(LocalDateTime createdAtAfter, LocalDateTime createdAtBefore);

    /* 칭찬 날짜 + 보낸 사람 */
    List<Praise> findBySenderAndCreatedAtBetween(User sender, LocalDateTime createdAtAfter, LocalDateTime createdAtBefore);

    /* 전체 칭찬 + 칭찬 보내는 사람 받는 사람 keyword 포함 */
    @Query("SELECT DISTINCT p FROM Praise p " +
            "JOIN PraiseReceiver pr ON pr.praise = p " +
            "WHERE p.createdAt BETWEEN :startDateTime AND :endDateTime " +
            "AND (p.sender.name LIKE %:keyword% OR pr.receiver.name LIKE %:keyword%)")
    List<Praise> findAllPraisesBySenderOrReceiverNameContaining(LocalDateTime startDateTime, LocalDateTime endDateTime, String keyword);

    /* 내가 보낸 칭찬들 중 keyword 포함 */
    @Query("SELECT DISTINCT p FROM Praise p " +
            "JOIN PraiseReceiver pr ON pr.praise = p " +
            "WHERE p.sender = :currentUser " +
            "AND p.createdAt BETWEEN :startDateTime AND :endDateTime " +
            "AND (pr.receiver.name LIKE %:keyword% OR p.sender.name LIKE %:keyword%)")
    List<Praise> findMySentPraiseWithKeyword(User currentUser, LocalDateTime startDateTime, LocalDateTime endDateTime, String keyword);


}
