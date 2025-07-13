package org.example.hugmeexp.domain.mission.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.hugmeexp.domain.mission.dto.response.UserMissionResponse;
import org.example.hugmeexp.domain.mission.dto.response.UserMissionStateLogResponse;
import org.example.hugmeexp.domain.mission.entity.Mission;
import org.example.hugmeexp.domain.mission.entity.UserMission;
import org.example.hugmeexp.domain.mission.entity.UserMissionStateLog;
import org.example.hugmeexp.domain.mission.enums.UserMissionState;
import org.example.hugmeexp.domain.mission.exception.AlreadyExistsUserMissionException;
import org.example.hugmeexp.domain.mission.exception.MissionNotFoundException;
import org.example.hugmeexp.domain.mission.exception.UserMissionNotFoundException;
import org.example.hugmeexp.domain.mission.mapper.UserMissionMapper;
import org.example.hugmeexp.domain.mission.mapper.UserMissionStateLogMapper;
import org.example.hugmeexp.domain.mission.repository.MissionRepository;
import org.example.hugmeexp.domain.mission.repository.UserMissionRepository;
import org.example.hugmeexp.domain.mission.repository.UserMissionStateLogRepository;
import org.example.hugmeexp.domain.missionGroup.entity.MissionGroup;
import org.example.hugmeexp.domain.missionGroup.entity.UserMissionGroup;
import org.example.hugmeexp.domain.missionGroup.exception.UserMissionGroupNotFoundException;
import org.example.hugmeexp.domain.missionGroup.exception.UserNotFoundException;
import org.example.hugmeexp.domain.missionGroup.repository.UserMissionGroupRepository;
import org.example.hugmeexp.domain.user.entity.User;
import org.example.hugmeexp.domain.user.repository.UserRepository;
import org.example.hugmeexp.global.common.service.CacheService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserMissionServiceImpl implements UserMissionService {
    private final MissionRepository missionRepository;
    private final UserMissionRepository userMissionRepository;
    private final UserMissionGroupRepository userMissionGroupRepository;
    private final UserRepository userRepository;
    private final UserMissionMapper userMissionMapper;
    private final UserMissionStateLogRepository userMissionStateLogRepository;
    private final UserMissionStateLogMapper userMissionStateLogMapper;

    private final CacheService cacheService;


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
        userMissionStateLogRepository.save(UserMissionStateLog.builder()
                .userMission(userMission)
                .prevState(null)
                .nextState(UserMissionState.NOT_STARTED)
                .build());

        cacheService.evictUserCache(username);

        return userMissionMapper.toUserMissionResponse(userMissionRepository.save(userMission));
    }

    @Override
    @Transactional
    public void changeUserMissionState(Long userMissionId, UserMissionState newProgress) {
        UserMission userMission = userMissionRepository.findByIdWithUser(userMissionId)
                .orElseThrow(UserMissionNotFoundException::new);
        userMissionStateLogRepository.save(UserMissionStateLog.builder()
                .userMission(userMission)
                .prevState(userMission.getProgress())
                .nextState(newProgress)
                .build());
        userMission.setProgress(newProgress);


        userMissionRepository.save(userMission);
        cacheService.evictUserCache(userMission.getUser().getUsername());
    }

    @Override
    public List<UserMissionStateLogResponse> getAllMissionStateLog(long userId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();
        return userMissionStateLogRepository.findByUserIdAndCreatedAtBetween(userId, startDateTime, endDateTime)
                .stream().map(userMissionStateLogMapper::toUserMissionStateLogResponse).collect(Collectors.toList());
    }

    @Override
    public UserMissionResponse getUserMission(Long missionId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);

        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(MissionNotFoundException::new);

        UserMission userMissions = userMissionRepository.findByUserAndMission(user, mission)
                .orElseThrow(UserMissionNotFoundException::new);

        return userMissionMapper.toUserMissionResponse(userMissions);
    }

    @Override
    public List<UserMissionResponse> getAllUserMissionsByTeacher(String username) {
        User teacher = userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);
        List<UserMission> userMissions = userMissionRepository.findAllByMission_MissionGroup_Teacher(teacher);

        return userMissions
                .stream().map(userMissionMapper::toUserMissionResponse).collect(Collectors.toList());
    }

    @Override
    public UserMissionResponse getUserMissionByChallengeId(Long challengeId) {
        UserMission userMission = userMissionRepository.findById(challengeId)
                .orElseThrow(UserMissionNotFoundException::new);

        return userMissionMapper.toUserMissionResponse(userMission);
    }
}
