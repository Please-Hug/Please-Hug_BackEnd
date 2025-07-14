package org.example.hugmeexp.domain.missionGroup.mapper;

import org.example.hugmeexp.domain.missionGroup.dto.response.UserMissionGroupResponse;
import org.example.hugmeexp.domain.missionGroup.entity.UserMissionGroup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring",
uses = {MissionGroupMapper.class})
public interface UserMissionGroupMapper {
    @Mappings({
            @Mapping(target = "user", ignore = true),
    })
    UserMissionGroupResponse toUserMissionGroupResponse(UserMissionGroup userMissionGroup);

    @Mappings({
            @Mapping(target = "user", ignore = true),
            @Mapping(target = "missionGroup.teacher", ignore = true)
    })
    UserMissionGroupResponse exceptUserAndTeacher(UserMissionGroup userMissionGroup);
}
