package org.example.hugmeexp.domain.mission.service;

import org.example.hugmeexp.domain.mission.dto.response.UserMissionResponse;
import org.example.hugmeexp.domain.mission.dto.response.UserMissionStateLogResponse;
import org.example.hugmeexp.domain.mission.enums.UserMissionState;

import java.time.LocalDate;
import java.util.List;

public interface UserMissionService {
    UserMissionResponse challengeMission(String username, Long missionId);

    void changeUserMissionState(Long userMissionId, UserMissionState newProgress);

    List<UserMissionStateLogResponse> getAllMissionStateLog(long userId, LocalDate startDate, LocalDate endDate);

    UserMissionResponse getUserMission(Long missionId, String username);

    List<UserMissionResponse> getAllUserMissionsByTeacher(String username);

    UserMissionResponse getUserMissionByChallengeId(Long challengeId);
}
