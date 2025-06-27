package org.example.hugmeexp.domain.missionGroup.mapper;

import org.example.hugmeexp.domain.missionGroup.dto.response.UserMissionGroupResponse;
import org.example.hugmeexp.domain.missionGroup.entity.UserMissionGroup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring",
uses = {MissionGroupMapper.class})
public interface UserMissionGroupMapper {
    @Mappings({
            @Mapping(target = "user", ignore = true),
    })
    UserMissionGroupResponse toUserMissionGroupResponse(UserMissionGroup userMissionGroup);
}
