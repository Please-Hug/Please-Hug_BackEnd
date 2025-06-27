package org.example.hugmeexp.domain.praise.repository;

import org.example.hugmeexp.domain.praise.entity.CommentEmojiReaction;
import org.example.hugmeexp.domain.praise.entity.Praise;
import org.example.hugmeexp.domain.praise.entity.PraiseEmojiReaction;
import org.example.hugmeexp.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PraiseEmojiReactionRepository extends JpaRepository<PraiseEmojiReaction, Long> {

//    @Query("SELECT r.emoji, COUNT(r) FROM PraiseEmojiReaction r WHERE r.praise = :praise GROUP BY r.emoji")
//    Map<String, Integer> countGroupedMapByPraise(Praise praise);

    /* 특정 칭찬에 대한 이모지별 반응 수 조회 (이모지, 개수 순으로 반환) */
//    @Query("SELECT r.emoji, COUNT(r) FROM PraiseEmojiReaction r " +
//            "WHERE r.praise = :praise GROUP BY r.emoji")
//    List<Object[]> countGroupedByEmoji(@Param("praise") Praise praise);

    /* 특정 칭찬 글에 달린 모든 이모지 반응을 조회 */
    List<PraiseEmojiReaction> findByPraise(Praise praise);

    /* 특정 칭찬 글에서 특정 이모지에 대한 반응만 조회 */
    List<PraiseEmojiReaction> findByPraiseAndEmoji(Praise praise, String emoji);

    /* 특정 이모지로 이미 반응한 적이 있는지 여부 반환 */
    boolean existsByPraiseAndReactorWriterAndEmoji(Praise praise, User reactorWriter, String emoji);
}
