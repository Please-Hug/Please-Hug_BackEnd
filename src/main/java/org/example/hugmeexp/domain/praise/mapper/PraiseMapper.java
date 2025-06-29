package org.example.hugmeexp.domain.praise.mapper;

import org.example.hugmeexp.domain.praise.dto.EmojiReactionGroupDTO;
import org.example.hugmeexp.domain.praise.dto.PraiseRequestDTO;
import org.example.hugmeexp.domain.praise.dto.PraiseResponseDTO;
import org.example.hugmeexp.domain.praise.entity.Praise;
import org.example.hugmeexp.domain.praise.entity.PraiseReceiver;
import org.example.hugmeexp.domain.user.dto.response.UserProfileResponse;
import org.example.hugmeexp.domain.user.entity.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PraiseMapper {

    default Praise toEntity(PraiseRequestDTO praiseRequestDTO, User senderId){
        return Praise.builder()
                .sender(senderId)
                .content(praiseRequestDTO.getContent())
                .praiseType(praiseRequestDTO.getType())
                .build();
    }


    default PraiseResponseDTO toDTO(Praise praise, List<PraiseReceiver> praiseReceivers, long commentCount, List<EmojiReactionGroupDTO> emojiGroups, List<UserProfileResponse> commentPro){

        return PraiseResponseDTO.from(praise, praiseReceivers, commentCount, emojiGroups,commentPro);
    }
}
