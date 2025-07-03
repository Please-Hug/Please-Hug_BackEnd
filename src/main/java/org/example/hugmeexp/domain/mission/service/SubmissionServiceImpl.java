package org.example.hugmeexp.domain.mission.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.hugmeexp.domain.mission.dto.request.SubmissionFeedbackRequest;
import org.example.hugmeexp.domain.mission.dto.request.SubmissionUploadRequest;
import org.example.hugmeexp.domain.mission.dto.response.SubmissionResponse;
import org.example.hugmeexp.domain.mission.entity.*;
import org.example.hugmeexp.domain.mission.enums.FileUploadType;
import org.example.hugmeexp.domain.mission.enums.UserMissionState;
import org.example.hugmeexp.domain.mission.exception.*;
import org.example.hugmeexp.domain.mission.mapper.UserMissionSubmissionMapper;
import org.example.hugmeexp.domain.mission.repository.*;
import org.example.hugmeexp.domain.mission.util.FileUploadUtils;
import org.example.hugmeexp.domain.missionGroup.exception.UserNotFoundException;
import org.example.hugmeexp.domain.user.entity.User;
import org.example.hugmeexp.domain.user.repository.UserRepository;
import org.example.hugmeexp.domain.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubmissionServiceImpl implements SubmissionService {
    private final UserMissionRepository userMissionRepository;
    private final UserMissionSubmissionRepository userMissionSubmissionRepository;
    private final UserMissionSubmissionMapper userMissionSubmissionMapper;
    private final UserService userService;
    private final MissionRewardExpLogRepository missionRewardExpLogRepository;
    private final MissionRewardPointLogRepository missionRewardPointLogRepository;
    private final UserMissionStateLogRepository userMissionStateLogRepository;
    private final UserRepository userRepository;


    @Override
    @Transactional
    public void submitChallenge(Long userMissionId, SubmissionUploadRequest submissionUploadRequest, MultipartFile file) {
        UserMission userMission = userMissionRepository.findById(userMissionId)
                .orElseThrow(UserMissionNotFoundException::new);

        if (userMissionSubmissionRepository.existsByUserMission(userMission)) {
            throw new AlreadyExistsUserMissionSubmissionException();
        }

        String uploadDir = FileUploadUtils.getUploadPath(FileUploadType.MISSION_UPLOADS).toString();

        Submission submission = userMissionSubmissionMapper.toEntity(submissionUploadRequest);

        submission.setUserMission(userMission);

        if (file == null || file.isEmpty()) {
            throw new SubmissionFileUploadException("파일이 비어있거나 존재하지 않습니다.");
        } else if (
                file.getOriginalFilename() == null ||
                        file.getOriginalFilename().isEmpty() ||
                        !file.getOriginalFilename().contains(".")
        ) {
            throw new SubmissionFileUploadException("파일 확장자가 없습니다.");
        }

        String originalFilename = file.getOriginalFilename();

        String safeFileName = FileUploadUtils.getSafeFileName(originalFilename);

        // UUID를 사용하여 파일 이름을 안전하게 생성 및 중복 방지
        String fileName = UUID.randomUUID().toString();
        File destinationFile = new File(uploadDir, fileName);

        submission.setFileName(fileName);
        submission.setOriginalFileName(safeFileName); // 복원될 파일명

        userMissionSubmissionRepository.save(submission);

        userMission.setProgress(UserMissionState.IN_FEEDBACK);
        // 파일 전송되기 전에 저장하고 파일 전송이 실패하면 롤백되므로(SubmissionFileUploadException)
        // 고아 파일, 고아 레코드가 남지 않을 것임

        try {
            file.transferTo(destinationFile);
        } catch (IOException e) {
            throw new SubmissionFileUploadException("파일 저장 중 오류가 발생했습니다."); // RuntimeException을 던져서 IOException이 나더라도 롤백되게 함
        }
    }

    @Override
    public SubmissionResponse getSubmissionByMissionId(Long userMissionId) {
        UserMission userMission = userMissionRepository.findById(userMissionId)
                .orElseThrow(UserMissionNotFoundException::new);

        Submission submission = userMissionSubmissionRepository.findByUserMission(userMission)
                .orElseThrow(SubmissionNotFoundException::new);

        return userMissionSubmissionMapper.toSubmissionResponse(submission);
    }

    @Override
    @Transactional
    public void updateSubmissionFeedback(Long userMissionId, SubmissionFeedbackRequest submissionFeedbackRequest) {
        UserMission userMission = userMissionRepository.findById(userMissionId)
                .orElseThrow(UserMissionNotFoundException::new);
        Submission submission = userMissionSubmissionRepository.findByUserMission(userMission)
                .orElseThrow(SubmissionNotFoundException::new);
        submission.setFeedback(submissionFeedbackRequest.getFeedback());
        userMission.setProgress(UserMissionState.FEEDBACK_COMPLETED);
        userMissionRepository.save(userMission);
        userMissionSubmissionRepository.save(submission);
    }

    @Override
    @Transactional
    public void receiveReward(Long userMissionId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);
        UserMission userMission = userMissionRepository.findById(userMissionId)
                .orElseThrow(UserMissionNotFoundException::new);
        Mission mission = userMission.getMission();
        if (userMission.getProgress() == UserMissionState.REWARD_RECEIVED) {
            throw new AlreadyReceivedRewardException();
        }
        if (userMission.getProgress() != UserMissionState.FEEDBACK_COMPLETED) {
            throw new InvalidUserMissionStateException();
        }
        userMissionStateLogRepository.save(UserMissionStateLog.builder()
                .userMission(userMission)
                .prevState(userMission.getProgress())
                .nextState(UserMissionState.REWARD_RECEIVED)
                .build());
        userMission.setProgress(UserMissionState.REWARD_RECEIVED);

        missionRewardExpLogRepository.save(
                MissionRewardExpLog.builder()
                        .userMission(userMission)
                        .prevExp(user.getExp())
                        .nextExp(user.getExp() + mission.getRewardExp())
                        .note("미션 보상 경험치")
                        .build()
        );
        userService.increaseExp(user, mission.getRewardExp());
        missionRewardPointLogRepository.save(
                MissionRewardPointLog.builder()
                        .userMission(userMission)
                        .prevPoint(user.getPoint())
                        .nextPoint(user.getPoint() + mission.getRewardPoint())
                        .note("미션 보상 구름조각")
                        .build()
        );
        userService.increasePoint(user, mission.getRewardPoint());
    }
}
