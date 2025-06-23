package org.example.hugmeexp.domain.praise.repository;

import org.example.hugmeexp.domain.praise.entity.CommentEmojiReaction;
import org.example.hugmeexp.domain.praise.entity.PraiseComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentEmojiReactionRepository extends JpaRepository<CommentEmojiReaction, Long> {

    /* 댓글에 달린 이모지를 종류별로 그룹핑하여 개수 반환 */
    @Query("SELECT r.emoji, COUNT(r) FROM CommentEmojiReaction r " +
            "WHERE r.commentId = :comment GROUP BY r.emoji")
    List<Object[]> countGroupedByEmoji(PraiseComment comment);
}
