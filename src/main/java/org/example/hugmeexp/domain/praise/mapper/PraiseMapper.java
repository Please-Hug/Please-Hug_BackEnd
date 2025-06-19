package org.example.hugmeexp.domain.praise.mapper;

import org.example.hugmeexp.domain.praise.dto.PraiseRequestDTO;
import org.example.hugmeexp.domain.praise.dto.PraiseResponseDTO;
import org.example.hugmeexp.domain.praise.entity.Praise;
import org.example.hugmeexp.global.entity.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PraiseMapper {

    default Praise toEntity(PraiseRequestDTO praiseRequestDTO, User senderId, User receiverId){
        return Praise.builder()
                .sender(senderId)
                .receiver(receiverId)
                .content(praiseRequestDTO.getContent())
                .praiseType(praiseRequestDTO.getType())
                .build();
    }


    default PraiseResponseDTO toDTO(Praise praise){
        return PraiseResponseDTO.builder()
                .id(praise.getId())
                .senderName(praise.getSender().getName())
                .receiverName(List.of(praise.getReceiver().getName()))
                .content(praise.getContent())
                .type(praise.getPraiseType())
                .categoryLabel(praise.getPraiseType().getLabel())
                .commentCount(0)
                .emojiReactionCount(null)
                .createdAt(praise.getCreatedAt())
                .build();
    }
}
