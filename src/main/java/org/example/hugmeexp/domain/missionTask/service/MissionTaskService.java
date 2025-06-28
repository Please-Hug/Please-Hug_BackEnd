package org.example.hugmeexp.domain.missionTask.service;

import org.example.hugmeexp.domain.missionTask.dto.request.MissionTaskRequest;
import org.example.hugmeexp.domain.missionTask.dto.response.MissionTaskResponse;
import org.example.hugmeexp.domain.missionTask.dto.response.UserMissionTaskResponse;
import org.example.hugmeexp.domain.missionTask.enums.TaskState;

import java.util.List;

public interface MissionTaskService {
    List<MissionTaskResponse> findByMissionId(Long missionId);

    List<UserMissionTaskResponse> findUserMissionTasksByUsernameAndMissionId(String username, Long missionId);

    MissionTaskResponse addMissionTask(Long missionId, MissionTaskRequest missionTaskRequest);

    void deleteMissionTask(Long missionTaskId);

    void updateMissionTask(Long missionTaskId, MissionTaskRequest request);

    void changeUserMissionTaskState(String username, Long missionTaskId, TaskState state);
}
