package org.example.hugmeexp.domain.mission.mapper;

import org.example.hugmeexp.domain.mission.dto.response.UserMissionResponse;
import org.example.hugmeexp.domain.mission.entity.UserMission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMissionMapper {
    UserMissionResponse toUserMissionResponse(UserMission save);
}
