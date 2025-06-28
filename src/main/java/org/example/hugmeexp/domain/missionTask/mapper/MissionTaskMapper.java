package org.example.hugmeexp.domain.missionTask.mapper;

import org.example.hugmeexp.domain.missionTask.dto.request.MissionTaskRequest;
import org.example.hugmeexp.domain.missionTask.dto.response.MissionTaskResponse;
import org.example.hugmeexp.domain.missionTask.dto.response.UserMissionTaskResponse;
import org.example.hugmeexp.domain.missionTask.entity.MissionTask;
import org.example.hugmeexp.domain.missionTask.entity.UserMissionTask;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface MissionTaskMapper {
    @Mappings({
            @Mapping(target = "mission_id", source = "mission.id"),
    })
    MissionTaskResponse toMissionTaskResponse(MissionTask missionTask);
    MissionTask toEntity(MissionTaskRequest missionTaskRequest);

    @Mappings({
            @Mapping(target = "missionTaskId", source = "missionTask.id"),
            @Mapping(target = "userMissionId", source = "userMission.id")
    })
    UserMissionTaskResponse toUserMissionTaskResponse(UserMissionTask userMissionTask);
}
