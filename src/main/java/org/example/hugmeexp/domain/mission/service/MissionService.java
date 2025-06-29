package org.example.hugmeexp.domain.mission.service;
import org.example.hugmeexp.domain.mission.dto.request.MissionRequest;
import org.example.hugmeexp.domain.mission.dto.request.SubmissionFeedbackRequest;
import org.example.hugmeexp.domain.mission.dto.request.SubmissionUploadRequest;
import org.example.hugmeexp.domain.mission.dto.response.MissionResponse;
import org.example.hugmeexp.domain.mission.dto.response.SubmissionResponse;
import org.example.hugmeexp.domain.mission.dto.response.UserMissionResponse;
import org.example.hugmeexp.domain.mission.dto.response.UserMissionStateLogResponse;
import org.example.hugmeexp.domain.mission.entity.UserMissionStateLog;
import org.example.hugmeexp.domain.mission.enums.UserMissionState;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface MissionService {
    MissionResponse createMission(MissionRequest missionRequest);

    MissionResponse getMissionById(Long id);

    List<MissionResponse> getAllMissions();

    MissionResponse updateMission(Long id, MissionRequest missionRequest);

    void deleteMission(Long id);

    MissionResponse changeMissionGroup(Long id, Long missionGroupId);

    List<MissionResponse> getMissionsByMissionGroupId(Long missionGroupId);

    UserMissionResponse challengeMission(String username, Long missionId);

    void changeUserMissionState(Long userMissionId, UserMissionState newProgress);

    void submitChallenge(Long userMissionId, SubmissionUploadRequest submissionUploadRequest, MultipartFile file);

    SubmissionResponse getSubmissionByMissionId(Long userMissionId);

    void updateSubmissionFeedback(Long userMissionId, SubmissionFeedbackRequest submissionFeedbackRequest);

    void receiveReward(Long userMissionId, String username);

    List<UserMissionStateLogResponse> getAllMissionStateLog(long userId, LocalDate startDate, LocalDate endDate);
}
