package org.example.hugmeexp.domain.mission.service;

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
import org.example.hugmeexp.domain.mission.repository.*;
import org.example.hugmeexp.domain.missionGroup.entity.MissionGroup;
import org.example.hugmeexp.domain.missionGroup.entity.UserMissionGroup;
import org.example.hugmeexp.domain.missionGroup.exception.UserMissionGroupNotFoundException;
import org.example.hugmeexp.domain.missionGroup.exception.UserNotFoundException;
import org.example.hugmeexp.domain.missionGroup.repository.UserMissionGroupRepository;
import org.example.hugmeexp.domain.user.entity.User;
import org.example.hugmeexp.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserMissionService 테스트")
class UserMissionServiceTest {
    @Mock
    private MissionRepository missionRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMissionRepository userMissionRepository;
    @Mock
    private UserMissionStateLogRepository userMissionStateLogRepository;
    @Mock
    private UserMissionGroupRepository userMissionGroupRepository;

    @Mock
    private UserMissionMapper userMissionMapper;

    @Mock
    private UserMissionStateLogMapper userMissionStateLogMapper;

    @InjectMocks
    private UserMissionServiceImpl userMissionService;




    @Test
    @DisplayName("유저가 미션 그룹을 처음 도전할 때 새로운 UserMission 을 리턴한다 - 성공")
    void challengeMission_Success() {
        // given
        String username = "testUser";
        Long missionId = 100L;

        User user = mock(User.class);
        MissionGroup group = MissionGroup.builder().id(20L).build();
        Mission mission = Mission.builder()
                .id(missionId)
                .missionGroup(group)
                .build();
        UserMissionGroup omg = UserMissionGroup.builder()
                .id(30L)
                .user(user)
                .missionGroup(group)
                .build();
        UserMission savedUm = UserMission.builder()
                .id(40L)
                .user(user)
                .mission(mission)
                .userMissionGroup(omg)
                .progress(UserMissionState.NOT_STARTED)
                .build();
        UserMissionResponse expectedRes = UserMissionResponse.builder()
                .id(40L)
                .progress(UserMissionState.NOT_STARTED)
                .build();

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(user));
        when(missionRepository.findById(missionId))
                .thenReturn(Optional.of(mission));
        when(userMissionGroupRepository.findByUserAndMissionGroup(user, group))
                .thenReturn(Optional.of(omg));
        when(userMissionRepository.save(any(UserMission.class)))
                .thenReturn(savedUm);
        when(userMissionMapper.toUserMissionResponse(savedUm))
                .thenReturn(expectedRes);

        // when
        UserMissionResponse actual = userMissionService.challengeMission(username, missionId);

        // then
        assertThat(actual).isEqualTo(expectedRes);

        // Repository.save() 로 넘겨진 UserMission 의 상태 검증
        ArgumentCaptor<UserMission> captor = ArgumentCaptor.forClass(UserMission.class);
        verify(userMissionRepository).save(captor.capture());
        UserMission toSave = captor.getValue();
        assertThat(toSave.getUser()).isSameAs(user);
        assertThat(toSave.getMission()).isSameAs(mission);
        assertThat(toSave.getUserMissionGroup()).isSameAs(omg);
        assertThat(toSave.getProgress()).isEqualTo(UserMissionState.NOT_STARTED);

        verify(userMissionMapper).toUserMissionResponse(savedUm);
    }

    @Test
    @DisplayName("존재하지 않는 유저로 challengeMission 호출 시 UserNotFoundException 발생")
    void challengeMission_UserNotFound() {
        when(userRepository.findByUsername("nope"))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userMissionService.challengeMission("nope", 123L));
    }

    @Test
    @DisplayName("존재하지 않는 미션으로 challengeMission 호출 시 MissionNotFoundException 발생")
    void challengeMission_MissionNotFound() {
        String username = "testUser";
        when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(mock(User.class)));
        when(missionRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(MissionNotFoundException.class,
                () -> userMissionService.challengeMission(username, 999L));
    }

    @Test
    @DisplayName("유저가 소속되지 않은 미션 그룹 도전 시 UserMissionGroupNotFoundException 발생")
    void challengeMission_UserMissionGroupNotFound() {
        String username = "testUser";
        User user = mock(User.class);
        MissionGroup group = MissionGroup.builder().id(20L).build();
        Mission mission = Mission.builder().id(2L).missionGroup(group).build();

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(user));
        when(missionRepository.findById(2L))
                .thenReturn(Optional.of(mission));
        when(userMissionGroupRepository.findByUserAndMissionGroup(user, group))
                .thenReturn(Optional.empty());

        assertThrows(UserMissionGroupNotFoundException.class,
                () -> userMissionService.challengeMission(username, 2L));
    }


    @Test
    @DisplayName("UserMission 상태를 완료로 변경한다 - 성공")
    void changeUserMissionState_Success() {
        // given
        Long userMissionId = 55L;
        UserMission existing = UserMission.builder()
                .id(userMissionId)
                .progress(UserMissionState.NOT_STARTED)
                .build();

        when(userMissionRepository.findById(userMissionId))
                .thenReturn(Optional.of(existing));
        when(userMissionStateLogRepository.save(any()))
                .thenReturn(mock(UserMissionStateLog.class));

        // when
        userMissionService.changeUserMissionState(userMissionId, UserMissionState.COMPLETED);

        // then
        ArgumentCaptor<UserMission> captor = ArgumentCaptor.forClass(UserMission.class);
        verify(userMissionRepository).save(captor.capture());
        UserMission saved = captor.getValue();
        assertThat(saved.getId()).isEqualTo(userMissionId);
        assertThat(saved.getProgress()).isEqualTo(UserMissionState.COMPLETED);
    }

    @Test
    @DisplayName("존재하지 않는 UserMission 상태 변경 시 UserMissionNotFoundException 발생")
    void changeUserMissionState_NotFound() {
        when(userMissionRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(UserMissionNotFoundException.class,
                () -> userMissionService.changeUserMissionState(999L, UserMissionState.COMPLETED));
    }

    @Test
    @DisplayName("이미 시도한 UserMission에 대해 다시 도전 시도 시 AlreadyExistsUserMissionException 발생")
    void challengeMission_AlreadyExists() {
        // given
        String username = "testUser";
        Long missionId = 100L;

        User user = mock(User.class);
        Mission mission = Mission.builder().id(missionId).build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(missionRepository.findById(missionId)).thenReturn(Optional.of(mission));
        when(userMissionRepository.existsUserMissionByUserAndMission(user, mission)).thenReturn(true);

        // when & then
        assertThrows(AlreadyExistsUserMissionException.class,
                () -> userMissionService.challengeMission(username, missionId));
    }

    @Test
    @DisplayName("유저의 미션 상태 로그를 조회한다 - 성공")
    void getAllMissionStateLog_Success() {
        // given
        long userId = 1L;
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);

        when(userMissionStateLogRepository.findByUserIdAndCreatedAtBetween(
                anyLong(),
                any(LocalDateTime.class),
                any(LocalDateTime.class)

        )).thenReturn(List.of(
                mock(UserMissionStateLog.class),
                mock(UserMissionStateLog.class)
        ));

        when(userMissionStateLogMapper.toUserMissionStateLogResponse(any(UserMissionStateLog.class)))
                .thenReturn(mock(UserMissionStateLogResponse.class));

        // when
        List<UserMissionStateLogResponse> logs = userMissionService.getAllMissionStateLog(userId, startDate, endDate);

        assertThat(logs).hasSize(2);
    }
    
    @Test
    @DisplayName("유저 미션을 가져온다 - 성공")
    void getUserMission_Success() {
        // given
        Long missionId = 1L;
        String username = "testUser";

        User user = mock(User.class);
        Mission mission = mock(Mission.class);
        UserMission userMission = mock(UserMission.class);
        UserMissionResponse expectedResponse = mock(UserMissionResponse.class);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(missionRepository.findById(missionId)).thenReturn(Optional.of(mission));
        when(userMissionRepository.findByUserAndMission(user, mission)).thenReturn(Optional.of(userMission));
        when(userMissionMapper.toUserMissionResponse(userMission)).thenReturn(expectedResponse);

        // when
        UserMissionResponse actualResponse = userMissionService.getUserMission(missionId, username);

        // then
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("존재하지 않는 유저의 미션을 가져올 때 UserNotFoundException 발생 - 실패")
    void getUserMission_UserNotFound() {
        Long missionId = 1L;
        String username = "nonExistentUser";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userMissionService.getUserMission(missionId, username));
    }

    @Test
    @DisplayName("존재하지 않는 미션의 유저 미션을 가져올 때 MissionNotFoundException 발생 - 실패")
    void getUserMission_MissionNotFound() {
        Long missionId = 1L;
        String username = "testUser";

        User user = mock(User.class);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(missionRepository.findById(missionId)).thenReturn(Optional.empty());

        assertThrows(MissionNotFoundException.class, () -> userMissionService.getUserMission(missionId, username));
    }

    @Test
    @DisplayName("존재하지 않는 유저 미션을 가져올 때 UserMissionNotFoundException 발생 - 실패")
    void getUserMission_UserMissionNotFound() {
        Long missionId = 1L;
        String username = "testUser";

        User user = mock(User.class);
        Mission mission = mock(Mission.class);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(missionRepository.findById(missionId)).thenReturn(Optional.of(mission));
        when(userMissionRepository.findByUserAndMission(user, mission)).thenReturn(Optional.empty());

        assertThrows(UserMissionNotFoundException.class, () -> userMissionService.getUserMission(missionId, username));
    }

    @Test
    @DisplayName("선생님이 관리하는 모든 유저 미션을 조회한다 - 성공")
    void getAllUserMissionsByTeacher_Success() {
        // given
        String username = "teacherUser";
        User teacher = mock(User.class);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(teacher));

        List<UserMission> userMissions = List.of(mock(UserMission.class), mock(UserMission.class));
        when(userMissionRepository.findAllByMission_MissionGroup_Teacher(teacher)).thenReturn(userMissions);

        UserMissionResponse response1 = mock(UserMissionResponse.class);
        UserMissionResponse response2 = mock(UserMissionResponse.class);
        when(userMissionMapper.toUserMissionResponse(userMissions.get(0))).thenReturn(response1);
        when(userMissionMapper.toUserMissionResponse(userMissions.get(1))).thenReturn(response2);

        // when
        List<UserMissionResponse> responses = userMissionService.getAllUserMissionsByTeacher(username);

        // then
        assertThat(responses).containsExactly(response1, response2);
    }

    @Test
    @DisplayName("존재하지 않는 선생님의 유저 미션을 조회할 때 UserNotFoundException 발생 - 실패")
    void getAllUserMissionsByTeacher_UserNotFound() {
        // given
        String username = "nonExistentTeacher";
        // when
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        // then
        assertThrows(UserNotFoundException.class, () -> userMissionService.getAllUserMissionsByTeacher(username));
    }

    @Test
    @DisplayName("특정 챌린지 ID로 유저 미션을 조회한다 - 성공")
    void getUserMissionByChallengeId_Success() {
        // given
        Long challengeId = 1L;
        UserMission userMission = mock(UserMission.class);
        when(userMissionRepository.findById(challengeId)).thenReturn(Optional.of(userMission));

        UserMissionResponse expectedResponse = mock(UserMissionResponse.class);
        when(userMissionMapper.toUserMissionResponse(userMission)).thenReturn(expectedResponse);

        // when
        UserMissionResponse actualResponse = userMissionService.getUserMissionByChallengeId(challengeId);

        // then
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("존재하지 않는 챌린지 ID로 유저 미션을 조회할 때 UserMissionNotFoundException 발생 - 실패")
    void getUserMissionByChallengeId_NotFound() {
        // given
        Long challengeId = 999L;
        when(userMissionRepository.findById(challengeId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(UserMissionNotFoundException.class, () -> userMissionService.getUserMissionByChallengeId(challengeId));
    }
}
