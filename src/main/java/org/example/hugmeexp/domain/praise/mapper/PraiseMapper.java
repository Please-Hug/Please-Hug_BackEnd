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
                .senderId(senderId)
                .receiverId(receiverId)
                .content(praiseRequestDTO.getContent())
                .praiseType(praiseRequestDTO.getType())
                .build();
    };

//    Praise toEntity(PraiseRequestDTO praiseRequestDTO);
//
//    PraiseResponseDTO toDTO(Praise praise);

    default PraiseResponseDTO toDTO(Praise praise){
        return PraiseResponseDTO.builder()
                .id(praise.getId())
                .senderName(praise.getSenderId().getName())
                .receiverName(List.of(praise.getReceiverId().getName()))
                .content(praise.getContent())
                .type(praise.getPraiseType())
                .commentCount(0)
                .emojiReactionCount(null)
                .createdAt(praise.getCreatedAt())
                .build();
    }
}
