package org.example.hugmeexp.domain.praise.mapper;

import org.example.hugmeexp.domain.praise.dto.CommentEmojiReactionRequestDTO;
import org.example.hugmeexp.domain.praise.dto.CommentEmojiReactionResponseDTO;
import org.example.hugmeexp.domain.praise.entity.PraiseComment;
import org.example.hugmeexp.domain.praise.entity.CommentEmojiReaction;
import org.example.hugmeexp.domain.user.entity.User;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface CommentEmojiReactionMapper {

    default CommentEmojiReaction toEntity(CommentEmojiReactionRequestDTO commentEmojiReactionRequestDTO, PraiseComment comment, User user){
        return CommentEmojiReaction.builder()
                .comment(comment)
                .reactorWriter(user)
                .emoji(commentEmojiReactionRequestDTO.getEmoji())
                .build();
    }

    default CommentEmojiReactionResponseDTO toDTO(CommentEmojiReaction commentEmojiReaction){
        return CommentEmojiReactionResponseDTO.builder()
                .id(commentEmojiReaction.getId())
                .commentId(commentEmojiReaction.getComment().getId())
                .reactorName(commentEmojiReaction.getReactorWriter().getName())
                .emoji(commentEmojiReaction.getEmoji())
                .createdAt(commentEmojiReaction.getCreatedAt())
                .build();
    }
}

