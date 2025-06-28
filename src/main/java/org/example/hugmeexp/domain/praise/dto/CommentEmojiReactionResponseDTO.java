package org.example.hugmeexp.domain.praise.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hugmeexp.domain.praise.entity.CommentEmojiReaction;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentEmojiReactionResponseDTO {

    private Long id;    // PK

    private Long commentId;    // 댓글 반응일 경우에만 값 존재

    private String reactorName;    // 반응한 사람 이름
    private String emoji;    // 이모지 값
    private LocalDateTime createdAt;    // 반응한 시간

    public static CommentEmojiReactionResponseDTO from(CommentEmojiReaction reaction){
        return CommentEmojiReactionResponseDTO.builder()
                .id(reaction.getId())
                .commentId(reaction.getComment().getId())
                .reactorName(reaction.getReactorWriter().getName())
                .emoji(reaction.getEmoji())
                .createdAt(reaction.getCreatedAt())
                .build();
    }
}
