package org.example.hugmeexp.domain.missionGroup.mapper;

import org.example.hugmeexp.domain.missionGroup.dto.response.MissionGroupResponse;
import org.example.hugmeexp.domain.missionGroup.entity.MissionGroup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface MissionGroupMapper {
    @Mappings(
            @Mapping(target = "teacher.profileImage", source = "teacher.publicProfileImageUrl")
    )
    MissionGroupResponse toMissionGroupResponse(MissionGroup missionGroup);
}
