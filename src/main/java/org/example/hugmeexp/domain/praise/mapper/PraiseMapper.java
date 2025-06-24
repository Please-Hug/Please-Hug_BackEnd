package org.example.hugmeexp.domain.praise.mapper;

import org.example.hugmeexp.domain.praise.dto.PraiseRequestDTO;
import org.example.hugmeexp.domain.praise.dto.PraiseResponseDTO;
import org.example.hugmeexp.domain.praise.entity.Praise;
import org.example.hugmeexp.domain.praise.entity.PraiseReceiver;
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


    // 댓글, 반응 만들면 매개변수 long commentCount, Map<String, Integer> emojiCount 으로 수정하기
    default PraiseResponseDTO toDTO(Praise praise, List<PraiseReceiver> praiseReceivers){

        List<String> receiverNames = praiseReceivers.stream()
                .map(praiseReceiver -> praiseReceiver.getReceiver().getName())
                .toList();

        return PraiseResponseDTO.builder()
                .id(praise.getId())
                .senderName(praise.getSender().getName())
                .receiverName(receiverNames)
                .content(praise.getContent())
                .type(praise.getPraiseType())
                .commentCount(0)
                .emojiReactionCount(null)
                .createdAt(praise.getCreatedAt())
                .build();
    }
}
