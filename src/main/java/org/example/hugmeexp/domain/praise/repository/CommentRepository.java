package org.example.hugmeexp.domain.praise.repository;

import org.example.hugmeexp.domain.praise.entity.Praise;
import org.example.hugmeexp.domain.praise.entity.PraiseComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<PraiseComment, Long> {

    /* 해당 칭찬에 달린 댓글 수 조회 */
    long countByPraise(Praise praise);

//    /* 해당 칭찬에 달린 댓글 전체 목록 조회 */
//    List<PraiseComment> findByPraise(Praise praise);

    // 칭찬 게시물과 댓글 작성자 정보와 함께 조회 - 성능개선
    @Query("SELECT c FROM PraiseComment c JOIN FETCH c.commentWriter WHERE c.praise = :praise")
    List<PraiseComment> findWithWriterByPraise(@Param("praise") Praise praise);

    /* 해당 기간 내 칭찬 게시물 댓글 조회 */
    @Query("SELECT c FROM PraiseComment c " +
            "WHERE c.commentWriter.id = :userId "+
            "AND c.createdAt BETWEEN :start AND :end")
    List<PraiseComment> findCommentsByUserAndPeriod(Long userId, LocalDateTime start, LocalDateTime end);
}
