package org.example.hugmeexp.domain.missionGroup.mapper;

//import org.example.hugmeexp.domain.missionGroup.dto.request.MissionGroupRequest;
import org.example.hugmeexp.domain.missionGroup.dto.response.MissionGroupResponse;
import org.example.hugmeexp.domain.missionGroup.entity.MissionGroup;
import org.example.hugmeexp.domain.user.mapper.ProfileImageMapper;
import org.mapstruct.Mapper;
//import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
uses = ProfileImageMapper.class)
public interface MissionGroupMapper {
    MissionGroupResponse toMissionGroupResponse(MissionGroup missionGroup);
//    @Mapping(target = "id", ignore = true)
//    MissionGroup toEntity(MissionGroupRequest missionGroup);
}
