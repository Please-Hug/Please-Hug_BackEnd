package org.example.hugmeexp.domain.mission.mapper;

import org.example.hugmeexp.domain.mission.dto.response.UserMissionStateLogResponse;
import org.example.hugmeexp.domain.mission.entity.UserMissionStateLog;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",
uses = {UserMissionMapper.class})
public interface UserMissionStateLogMapper {
    UserMissionStateLogResponse toUserMissionStateLogResponse(UserMissionStateLog userMissionStateLog);
}
