package org.example.hugmeexp.domain.mission.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.hugmeexp.domain.mission.dto.request.MissionRequest;
import org.example.hugmeexp.domain.mission.dto.request.SubmissionFeedbackRequest;
import org.example.hugmeexp.domain.mission.dto.request.SubmissionUploadRequest;
import org.example.hugmeexp.domain.mission.dto.response.MissionResponse;
import org.example.hugmeexp.domain.mission.dto.response.SubmissionResponse;
import org.example.hugmeexp.domain.mission.dto.response.UserMissionResponse;
import org.example.hugmeexp.domain.mission.entity.Mission;
import org.example.hugmeexp.domain.mission.entity.UserMission;
import org.example.hugmeexp.domain.mission.entity.Submission;
import org.example.hugmeexp.domain.mission.enums.FileUploadType;
import org.example.hugmeexp.domain.mission.enums.UserMissionState;
import org.example.hugmeexp.domain.mission.exception.*;
import org.example.hugmeexp.domain.mission.mapper.MissionMapper;
import org.example.hugmeexp.domain.mission.mapper.UserMissionMapper;
import org.example.hugmeexp.domain.mission.mapper.UserMissionSubmissionMapper;
import org.example.hugmeexp.domain.mission.repository.MissionRepository;
import org.example.hugmeexp.domain.mission.repository.UserMissionRepository;
import org.example.hugmeexp.domain.mission.repository.UserMissionSubmissionRepository;
import org.example.hugmeexp.domain.mission.util.FileUploadUtils;
import org.example.hugmeexp.domain.missionGroup.entity.MissionGroup;
import org.example.hugmeexp.domain.missionGroup.entity.UserMissionGroup;
import org.example.hugmeexp.domain.missionGroup.exception.MissionGroupNotFoundException;
import org.example.hugmeexp.domain.missionGroup.repository.MissionGroupRepository;
import org.example.hugmeexp.domain.missionGroup.repository.UserMissionGroupRepository;
import org.example.hugmeexp.domain.user.entity.User;
import org.example.hugmeexp.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.example.hugmeexp.domain.missionGroup.exception.UserNotFoundException;
import org.example.hugmeexp.domain.missionGroup.exception.UserMissionGroupNotFoundException;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MissionServiceImpl implements MissionService {
    private final MissionRepository missionRepository;
    private final MissionGroupRepository missionGroupRepository;
    private final UserMissionRepository userMissionRepository;
    private final UserMissionGroupRepository userMissionGroupRepository;
    private final UserRepository userRepository;
    private final MissionMapper missionMapper;
    private final UserMissionMapper userMissionMapper;
    private final UserMissionSubmissionRepository userMissionSubmissionRepository;
    private final UserMissionSubmissionMapper userMissionSubmissionMapper;

    @Override
    @Transactional
    public MissionResponse createMission(MissionRequest missionRequest) {
        Mission mission = missionMapper.toEntity(missionRequest);

        MissionGroup missionGroup = missionGroupRepository.findById(missionRequest.getMissionGroupId())
                .orElseThrow(MissionGroupNotFoundException::new);

        mission = mission.toBuilder()
                .missionGroup(missionGroup)
                .build();

        Mission savedMission = missionRepository.save(mission);
        return missionMapper.toMissionResponse(savedMission);
    }

    @Override
    public MissionResponse getMissionById(Long id) {
        Mission mission = missionRepository.findById(id)
                .orElseThrow(MissionNotFoundException::new);
        return missionMapper.toMissionResponse(mission);
    }

    @Override
    public List<MissionResponse> getAllMissions() {
        List<Mission> missions = missionRepository.findAll();
        return missions.stream()
                .map(missionMapper::toMissionResponse)
                .toList();
    }

    @Override
    @Transactional
    public MissionResponse updateMission(Long id, MissionRequest missionRequest) {
        Mission mission = missionRepository.findById(id)
                .orElseThrow(MissionNotFoundException::new);

        mission = mission.toBuilder()
                .name(missionRequest.getName())
                .description(missionRequest.getDescription())
                .difficulty(missionRequest.getDifficulty())
                .rewardPoint(missionRequest.getRewardPoint())
                .rewardExp(missionRequest.getRewardExp())
                .order(missionRequest.getOrder())
                .build();

        Mission updatedMission = missionRepository.save(mission);
        return missionMapper.toMissionResponse(updatedMission);
    }

    @Override
    @Transactional
    public void deleteMission(Long id) {
        Mission mission = missionRepository.findById(id)
                .orElseThrow(MissionNotFoundException::new);

        missionRepository.delete(mission);
    }

    @Override
    @Transactional
    public MissionResponse changeMissionGroup(Long id, Long missionGroupId) {
        Mission mission = missionRepository.findById(id)
                .orElseThrow(MissionNotFoundException::new);

        MissionGroup missionGroup = missionGroupRepository.findById(missionGroupId)
                .orElseThrow(MissionGroupNotFoundException::new);

        mission = mission.toBuilder()
                .missionGroup(missionGroup)
                .build();

        Mission updatedMission = missionRepository.save(mission);
        return missionMapper.toMissionResponse(updatedMission);
    }

    @Override
    public List<MissionResponse> getMissionsByMissionGroupId(Long missionGroupId) {
        MissionGroup missionGroup = missionGroupRepository.findById(missionGroupId)
                .orElseThrow(MissionGroupNotFoundException::new);

        List<Mission> missions = missionRepository.findMissionByMissionGroup(missionGroup);

        return missions.stream()
                .map(missionMapper::toMissionResponse)
                .toList();
    }

    @Override
    @Transactional
    public UserMissionResponse challengeMission(String username, Long missionId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);

        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(MissionNotFoundException::new);

        if (userMissionRepository.existsUserMissionByUserAndMission(user, mission)) {
            throw new AlreadyExistsUserMissionException();
        }

        MissionGroup missionGroup = mission.getMissionGroup();

        UserMissionGroup userMissionGroup = userMissionGroupRepository.findByUserAndMissionGroup(user, missionGroup)
                .orElseThrow(UserMissionGroupNotFoundException::new);

        UserMission userMission = UserMission.builder()
                .user(user)
                .mission(mission)
                .userMissionGroup(userMissionGroup)
                .progress(UserMissionState.NOT_STARTED)
                .build();

        return userMissionMapper.toUserMissionResponse(userMissionRepository.save(userMission));
    }

    @Override
    @Transactional
    public void changeUserMissionState(Long userMissionId, UserMissionState newProgress) {
        UserMission userMission = userMissionRepository.findById(userMissionId)
                .orElseThrow(UserMissionNotFoundException::new);

        userMission.setProgress(newProgress);

        userMissionRepository.save(userMission);
    }

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

        if (file == null || file.isEmpty())
        {
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
        userMission.setProgress(UserMissionState.REWARD_RECEIVED);
        user.increasePoint(mission.getRewardPoint());
        user.increaseExp(mission.getRewardExp());
        userMissionRepository.save(userMission);
        userRepository.save(user);
    }
}
