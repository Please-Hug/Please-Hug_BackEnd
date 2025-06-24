package org.example.hugmeexp.domain.mission.mapper;

import org.example.hugmeexp.domain.mission.dto.response.UserMissionResponse;
import org.example.hugmeexp.domain.mission.entity.UserMission;
import org.example.hugmeexp.domain.user.mapper.ProfileImageMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",
uses = ProfileImageMapper.class)
public interface UserMissionMapper {
    UserMissionResponse toUserMissionResponse(UserMission userMission);
}
