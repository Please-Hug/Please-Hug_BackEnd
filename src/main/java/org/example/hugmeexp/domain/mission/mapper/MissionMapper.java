package org.example.hugmeexp.domain.mission.mapper;

import org.example.hugmeexp.domain.mission.dto.request.MissionRequest;
import org.example.hugmeexp.domain.mission.dto.response.MissionResponse;
import org.example.hugmeexp.domain.mission.entity.Mission;
import org.example.hugmeexp.domain.user.mapper.ProfileImageMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
uses = ProfileImageMapper.class)
public interface MissionMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "missionGroup", ignore = true)
    Mission toEntity(MissionRequest missionRequest);
    MissionResponse toMissionResponse(Mission mission);
}
