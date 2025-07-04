package org.example.hugmeexp.domain.mission.mapper;

import org.example.hugmeexp.domain.mission.dto.request.SubmissionUploadRequest;
import org.example.hugmeexp.domain.mission.dto.response.SubmissionResponse;
import org.example.hugmeexp.domain.mission.entity.Submission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
uses = {UserMissionMapper.class, MissionMapper.class})
public interface UserMissionSubmissionMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userMission", ignore = true)
    @Mapping(target = "originalFileName", ignore = true)
    @Mapping(target = "feedback", ignore = true)
    Submission toEntity(SubmissionUploadRequest submissionUploadRequest);

    @Mapping(target = "userMission", ignore = true)
    SubmissionResponse toSubmissionResponse(Submission submission);
}
