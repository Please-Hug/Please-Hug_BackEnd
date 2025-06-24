package org.example.hugmeexp.domain.qeust.mapper;

import org.example.hugmeexp.domain.qeust.dto.UserQuestResponse;
import org.example.hugmeexp.domain.qeust.entity.UserQuest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserQuestMapper {

    @Mapping(source = "id", target = "userQuestId")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "quest.name", target = "questName")
    @Mapping(expression = "java(userQuest.isCompleted() ? \"완료\" : \"진행중\")", target = "progress")
    UserQuestResponse toResponse(UserQuest userQuest);
}