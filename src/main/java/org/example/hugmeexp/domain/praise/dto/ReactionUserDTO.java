package org.example.hugmeexp.domain.praise.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hugmeexp.domain.praise.entity.PraiseEmojiReaction;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReactionUserDTO {

    private Long id;
    private String username;
    private String name;

    public static ReactionUserDTO from(PraiseEmojiReaction reaction){
        return ReactionUserDTO.builder()
                .id(reaction.getId())
                .username(reaction.getReactorWriter().getUsername())
                .name(reaction.getReactorWriter().getName())
                .build();
    }
}
