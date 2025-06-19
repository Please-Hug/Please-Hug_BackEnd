package org.example.hugmeexp.domain.praise.repository;

import org.example.hugmeexp.domain.praise.entity.CommentEmojiReaction;
import org.example.hugmeexp.domain.praise.entity.Praise;
import org.example.hugmeexp.domain.praise.entity.PraiseEmojiReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface PraiseEmojiReactionRepository extends JpaRepository<PraiseEmojiReaction, Long> {

//    @Query("SELECT r.emoji, COUNT(r) FROM PraiseEmojiReaction r WHERE r.praise = :praise GROUP BY r.emoji")
//    Map<String, Integer> countGroupedMapByPraise(Praise praise);

    @Query("SELECT r. emoji, COUNT(r) FROM PraiseEmojiReaction r " +
            "WHERE r.praise = :praise GROUP BY r.emoji")
    List<Object[]> countGroupedByEmoji(@Param("praise") Praise praise);
}
