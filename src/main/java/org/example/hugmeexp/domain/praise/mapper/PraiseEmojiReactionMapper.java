package org.example.hugmeexp.domain.praise.mapper;

import org.example.hugmeexp.domain.praise.dto.PraiseEmojiReactionRequestDTO;
import org.example.hugmeexp.domain.praise.entity.Praise;
import org.example.hugmeexp.domain.praise.entity.PraiseEmojiReaction;
import org.example.hugmeexp.domain.user.entity.User;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface PraiseEmojiReactionMapper {

    default PraiseEmojiReaction toEntity(Praise praise, User user, PraiseEmojiReactionRequestDTO praiseEmojiReactionRequestDTO){
        return PraiseEmojiReaction.builder()
                .praise(praise)
                .reactorWriter(user)
                .emoji(praiseEmojiReactionRequestDTO.getEmoji())
                .build();
    }
}
