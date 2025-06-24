package org.example.hugmeexp.domain.praise.repository;

import org.example.hugmeexp.domain.praise.entity.Praise;
import org.example.hugmeexp.domain.praise.entity.PraiseComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<PraiseComment, Long> {

    /* 해당 칭찬에 달린 댓글 수 조회 */
    long countByPraise(Praise praise);

    /* 해당 칭찬에 달린 댓글 전체 목록 조회 */
    List<PraiseComment> findByPraise(Praise praise);
}
