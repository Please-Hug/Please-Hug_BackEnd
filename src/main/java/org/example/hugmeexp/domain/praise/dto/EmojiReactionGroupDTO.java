package org.example.hugmeexp.domain.praise.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hugmeexp.domain.praise.entity.PraiseEmojiReaction;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmojiReactionGroupDTO {

    private String emoji;    // 이모지
    private int count;    // 이모지 개수
    private List<String> reactedBy;    // 한 이모지에 반응한 유저리스트

    public static EmojiReactionGroupDTO from(String emoji, List<PraiseEmojiReaction> reactions){
        List<String> reactedBy = reactions.stream().map(reaction ->reaction.getReactorWriter().getName()).toList();

        return EmojiReactionGroupDTO.builder()
                .emoji(emoji)
                .count(reactions.size())
                .reactedBy(reactedBy)
                .build();
    }
}
