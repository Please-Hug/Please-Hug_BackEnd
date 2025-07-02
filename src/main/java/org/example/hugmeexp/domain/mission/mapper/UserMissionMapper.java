package org.example.hugmeexp.domain.mission.mapper;

import org.example.hugmeexp.domain.mission.dto.response.UserMissionResponse;
import org.example.hugmeexp.domain.mission.entity.UserMission;
import org.example.hugmeexp.domain.user.mapper.ProfileImageMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        uses = {ProfileImageMapper.class}) // Assuming ProfileImageMapper is used for mapping profile images)
public interface UserMissionMapper {
    @Mapping(target = "user.profileImage", source = "user.publicProfileImageUrl")
    UserMissionResponse toUserMissionResponse(UserMission userMission);
}
