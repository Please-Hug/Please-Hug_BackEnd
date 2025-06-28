package org.example.hugmeexp.domain.missionTask.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.hugmeexp.domain.mission.entity.Mission;
import org.example.hugmeexp.domain.mission.entity.UserMission;
import org.example.hugmeexp.domain.mission.exception.MissionNotFoundException;
import org.example.hugmeexp.domain.mission.exception.UserMissionNotFoundException;
import org.example.hugmeexp.domain.mission.repository.MissionRepository;
import org.example.hugmeexp.domain.mission.repository.UserMissionRepository;
import org.example.hugmeexp.domain.missionTask.dto.request.MissionTaskRequest;
import org.example.hugmeexp.domain.missionTask.dto.response.MissionTaskResponse;
import org.example.hugmeexp.domain.missionTask.dto.response.UserMissionTaskResponse;
import org.example.hugmeexp.domain.missionTask.entity.MissionTask;
import org.example.hugmeexp.domain.missionTask.entity.UserMissionTask;
import org.example.hugmeexp.domain.missionTask.enums.TaskState;
import org.example.hugmeexp.domain.missionTask.exception.MissionTaskNotFoundException;
import org.example.hugmeexp.domain.missionTask.mapper.MissionTaskMapper;
import org.example.hugmeexp.domain.missionTask.repository.MissionTaskRepository;
import org.example.hugmeexp.domain.missionTask.repository.UserMissionTaskRepository;
import org.example.hugmeexp.domain.user.entity.User;
import org.example.hugmeexp.domain.user.exception.UserNotFoundException;
import org.example.hugmeexp.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MissionTaskServiceImpl implements MissionTaskService {
    private final MissionTaskRepository missionTaskRepository;
    private final UserMissionTaskRepository userMissionTaskRepository;
    private final MissionTaskMapper missionTaskMapper;
    private final UserRepository userRepository;
    private final UserMissionRepository userMissionRepository;
    private final MissionRepository missionRepository;

    @Override
    public List<MissionTaskResponse> findByMissionId(Long missionId) {
        return missionTaskRepository.findByMissionId(missionId)
                .stream()
                .map(missionTaskMapper::toMissionTaskResponse)
                .toList();
    }

    @Override
    public List<UserMissionTaskResponse> findUserMissionTasksByUsernameAndMissionId(String username, Long missionId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);

        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(MissionNotFoundException::new);

        UserMission userMission = userMissionRepository.findByUserAndMission(user, mission)
                .orElseThrow(UserMissionNotFoundException::new);

        List<UserMissionTask> userMissionTasks = userMissionTaskRepository.findByUserMission(userMission);

        return userMissionTasks.stream()
                .map(missionTaskMapper::toUserMissionTaskResponse)
                .toList();
    }

    @Override
    @Transactional
    public MissionTaskResponse addMissionTask(Long missionId, MissionTaskRequest missionTaskRequest) {
        MissionTask missionTask = missionTaskMapper.toEntity(missionTaskRequest);
        missionTask.setMission(missionRepository.findById(missionId).orElseThrow(MissionNotFoundException::new));
        MissionTask savedMissionTask = missionTaskRepository.save(missionTask);
        return missionTaskMapper.toMissionTaskResponse(savedMissionTask);
    }

    @Override
    @Transactional
    public void deleteMissionTask(Long missionTaskId) {
        if (!missionTaskRepository.existsById(missionTaskId)) {
            throw new MissionTaskNotFoundException();
        }
        missionTaskRepository.deleteById(missionTaskId);
    }

    @Override
    @Transactional
    public void updateMissionTask(Long missionTaskId, MissionTaskRequest request) {
        MissionTask existingMissionTask = missionTaskRepository.findById(missionTaskId)
                .orElseThrow(MissionTaskNotFoundException::new);

        existingMissionTask.setName(request.getName());
        existingMissionTask.setScore(request.getScore());
    }

    @Transactional
    @Override
    public void changeUserMissionTaskState(String username, Long missionTaskId, TaskState state) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);
        MissionTask missionTask = missionTaskRepository.findById(missionTaskId)
                .orElseThrow(MissionTaskNotFoundException::new);

        UserMission userMission = userMissionRepository.findByUserAndMission(user, missionTask.getMission())
                .orElseThrow(UserMissionNotFoundException::new);

        Optional<UserMissionTask> userMissionTask = userMissionTaskRepository.findByUserMission_UserAndMissionTask(user, missionTask);

        if (userMissionTask.isEmpty()) {
            userMissionTaskRepository.save(UserMissionTask.builder()
                    .userMission(userMission)
                    .missionTask(missionTask)
                    .state(state)
                    .build());
        } else {
            UserMissionTask existingUserMissionTask = userMissionTask.get();
            existingUserMissionTask.setState(state);
        }
    }
}
