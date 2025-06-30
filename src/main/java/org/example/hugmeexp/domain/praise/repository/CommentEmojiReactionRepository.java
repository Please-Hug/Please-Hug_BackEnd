package org.example.hugmeexp.domain.praise.repository;

import org.example.hugmeexp.domain.praise.entity.CommentEmojiReaction;
import org.example.hugmeexp.domain.praise.entity.Praise;
import org.example.hugmeexp.domain.praise.entity.PraiseComment;
import org.example.hugmeexp.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentEmojiReactionRepository extends JpaRepository<CommentEmojiReaction, Long> {

    /* 댓글에 달린 이모지를 종류별로 그룹핑하여 개수 반환 */
    @Query("SELECT r.emoji, COUNT(r) FROM CommentEmojiReaction r " +
            "WHERE r.comment = :comment GROUP BY r.emoji")
    List<Object[]> countGroupedByEmoji(PraiseComment comment);

    /* 이모지 중복 되는지 확인 */
    boolean existsByCommentAndReactorWriterAndEmoji(PraiseComment comment, User reactorWriter, String emoji);

    @Query("SELECT r FROM CommentEmojiReaction r WHERE r.comment.praise = :praise")
    List<CommentEmojiReaction> findByPraise(Praise praise);

    void deleteByComment(PraiseComment comment);

    CommentEmojiReaction findByCommentAndEmoji(PraiseComment comment, String emoji);
}
