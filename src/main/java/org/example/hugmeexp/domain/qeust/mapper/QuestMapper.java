package org.example.hugmeexp.domain.qeust.mapper;

import org.example.hugmeexp.domain.qeust.dto.QuestRequest;
import org.example.hugmeexp.domain.qeust.dto.QuestResponse;
import org.example.hugmeexp.domain.qeust.entity.Quest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface QuestMapper {

    Quest toEntity(QuestRequest request);

    QuestResponse toResponse(Quest quest);
}
