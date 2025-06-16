package org.example.hugmeexp.domain.mission.mapper;

import org.example.hugmeexp.domain.mission.dto.MissionGroupRequest;
import org.example.hugmeexp.domain.mission.dto.MissionGroupResponse;
import org.example.hugmeexp.domain.mission.entity.MissionGroup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MissionGroupMapper {
    MissionGroupResponse toMissionGroupResponse(MissionGroup missionGroup);
    @Mapping(target = "id", ignore = true)
    MissionGroup toEntity(MissionGroupRequest missionGroup);
}
