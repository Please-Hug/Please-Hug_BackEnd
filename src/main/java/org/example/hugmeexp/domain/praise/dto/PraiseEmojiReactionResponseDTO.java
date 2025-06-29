package org.example.hugmeexp.domain.praise.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hugmeexp.domain.praise.entity.PraiseEmojiReaction;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PraiseEmojiReactionResponseDTO {

    private Long id;    // PK
    private String emoji;    // 이모지
    private List<ReactionUserDTO> reactedBy;    // 반응을 남긴 사람
    private Long praiseId;    // 칭찬 게시물

    public static PraiseEmojiReactionResponseDTO from(PraiseEmojiReaction savedReaction, List<PraiseEmojiReaction> sameEmojiReactions){

        List<ReactionUserDTO> reactedBy = sameEmojiReactions.stream()
                .map(ReactionUserDTO::from)
                .distinct()
                .toList();
        return PraiseEmojiReactionResponseDTO.builder()
                .id(savedReaction.getId())
                .emoji(savedReaction.getEmoji())
                .reactedBy(reactedBy)
                .praiseId(savedReaction.getPraise().getId())
                .build();
    }
}
