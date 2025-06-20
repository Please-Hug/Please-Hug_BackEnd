package org.example.hugmeexp.domain.mission.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.hugmeexp.domain.mission.dto.request.MissionRequest;
import org.example.hugmeexp.domain.mission.dto.response.MissionResponse;
import org.example.hugmeexp.domain.mission.dto.response.UserMissionResponse;
import org.example.hugmeexp.domain.mission.entity.Mission;
import org.example.hugmeexp.domain.mission.entity.UserMission;
import org.example.hugmeexp.domain.mission.enums.UserMissionState;
import org.example.hugmeexp.domain.mission.exception.AlreadyExistsUserMissionException;
import org.example.hugmeexp.domain.mission.exception.MissionNotFoundException;
import org.example.hugmeexp.domain.mission.exception.UserMissionNotFoundException;
import org.example.hugmeexp.domain.mission.mapper.MissionMapper;
import org.example.hugmeexp.domain.mission.mapper.UserMissionMapper;
import org.example.hugmeexp.domain.mission.repository.MissionRepository;
import org.example.hugmeexp.domain.mission.repository.UserMissionRepository;
import org.example.hugmeexp.domain.missionGroup.entity.MissionGroup;
import org.example.hugmeexp.domain.missionGroup.entity.UserMissionGroup;
import org.example.hugmeexp.domain.missionGroup.exception.MissionGroupNotFoundException;
import org.example.hugmeexp.domain.missionGroup.repository.MissionGroupRepository;
import org.example.hugmeexp.domain.missionGroup.repository.UserMissionGroupRepository;
import org.example.hugmeexp.domain.user.entity.User;
import org.example.hugmeexp.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import org.example.hugmeexp.domain.missionGroup.exception.UserNotFoundException;
import org.example.hugmeexp.domain.missionGroup.exception.UserMissionGroupNotFoundException;

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

        userMission = userMission.toBuilder()
                .progress(newProgress)
                .build();

        userMissionRepository.save(userMission);
    }
}
