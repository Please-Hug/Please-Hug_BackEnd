package org.example.hugmeexp.domain.praise.repository;

import org.example.hugmeexp.domain.praise.entity.Praise;
import org.example.hugmeexp.domain.praise.entity.PraiseReceiver;
import org.example.hugmeexp.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PraiseReceiverRepository extends JpaRepository<PraiseReceiver, Long> {

    /* 여러 칭찬에 대응하는 칭찬 받는 사람들을 한 번에 조회 */
    List<PraiseReceiver> findByPraiseIn(List<Praise> praiseList);

    /* 내가 보낸 칭찬들 중 특정 날짜 범위에 해당하는 칭찬들 조회*/
    List<PraiseReceiver> findByReceiverAndCreatedAtBetween(User currentUser, LocalDateTime startDateTime, LocalDateTime endDateTime);

    /* 칭찬 받은 유저 중 키워드가 포함된 유저들 조회 */
    @Query("SELECT pr FROM PraiseReceiver pr " +
            "WHERE pr.receiver = :currentUser " +
            "AND pr.praise.createdAt BETWEEN :startDateTime AND :endDateTime " +
            "AND (pr.praise.sender.name LIKE %:keyword% OR pr.receiver.name LIKE %:keyword%)")
    List<PraiseReceiver> findRelatedPraiseByReceiverWithKeyword(User currentUser, LocalDateTime startDateTime, LocalDateTime endDateTime, String keyword);

    /* 칭찬 칭찬 비율(한달동안 받은 칭찬 종류 count) */
    @Query("SELECT pr.praise.praiseType, COUNT(pr) FROM PraiseReceiver pr " +
            "WHERE pr.receiver.id = :userId " +
            "AND pr.praise.createdAt BETWEEN :startDateTime AND :endDateTime " +
            "GROUP BY pr.praise.praiseType")
    List<Object[]> countPraiseTypeByUserInMonth(Long userId, LocalDateTime startDateTime, LocalDateTime endDateTime);

    /* 최근 칭찬 보낸 유저 조회 */
    @Query("SELECT pr.praise FROM PraiseReceiver pr " +
            "WHERE pr.receiver.id = :userId " +
            "AND pr.praise.createdAt IN (" +
            "   SELECT MAX(pr2.praise.createdAt) FROM PraiseReceiver pr2 " +
            "   WHERE pr2.receiver.id = :userId " +
            "   GROUP BY pr2.praise.sender.id) " +
            "ORDER BY pr.praise.createdAt DESC")
    List<Praise> findLatestPraisePerSender(Long userId);
}
