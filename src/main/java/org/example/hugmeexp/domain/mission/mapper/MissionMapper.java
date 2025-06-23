package org.example.hugmeexp.domain.mission.mapper;

import org.example.hugmeexp.domain.mission.dto.request.MissionRequest;
import org.example.hugmeexp.domain.mission.dto.response.MissionResponse;
import org.example.hugmeexp.domain.mission.entity.Mission;
import org.example.hugmeexp.domain.user.mapper.ProfileImageMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",
uses = ProfileImageMapper.class)
public interface MissionMapper {
    Mission toEntity(MissionRequest missionRequest);
    MissionResponse toMissionResponse(Mission mission);
}
