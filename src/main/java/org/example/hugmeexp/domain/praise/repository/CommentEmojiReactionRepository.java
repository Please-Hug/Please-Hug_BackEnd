package org.example.hugmeexp.domain.praise.repository;

import org.example.hugmeexp.domain.praise.entity.CommentEmojiReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentEmojiReactionRepository extends JpaRepository<CommentEmojiReaction, Long> {
}
