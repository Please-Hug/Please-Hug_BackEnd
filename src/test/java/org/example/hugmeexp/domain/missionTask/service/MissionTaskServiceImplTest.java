package org.example.hugmeexp.domain.missionTask.service;

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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("미션 태스크 서비스 테스트")
class MissionTaskServiceImplTest {
    @Mock MissionTaskRepository missionTaskRepository;
    @Mock UserMissionTaskRepository userMissionTaskRepository;
    @Mock MissionTaskMapper missionTaskMapper;
    @Mock UserRepository userRepository;
    @Mock UserMissionRepository userMissionRepository;
    @Mock MissionRepository missionRepository;
    @InjectMocks MissionTaskServiceImpl missionTaskService;

    private final Long SAMPLE_ID = 1L;
    private final String SAMPLE_USERNAME = "user1";

    @Test
    @DisplayName("미션 ID로 미션 태스크 목록 조회 - 성공")
    void findByMissionId_Success() {
        MissionTask task = mock(MissionTask.class);
        MissionTaskResponse response = mock(MissionTaskResponse.class);
        when(missionTaskRepository.findByMissionId(SAMPLE_ID)).thenReturn(List.of(task));
        when(missionTaskMapper.toMissionTaskResponse(task)).thenReturn(response);
        List<MissionTaskResponse> result = missionTaskService.findByMissionId(SAMPLE_ID);
        assertThat(result).containsExactly(response);
    }

    @Test
    @DisplayName("유저명+미션ID로 유저 미션 태스크 목록 조회 - 성공")
    void findUserMissionTasksByUsernameAndMissionId_Success() {
        User user = mock(User.class);
        Mission mission = mock(Mission.class);
        UserMission userMission = mock(UserMission.class);
        UserMissionTask userMissionTask = mock(UserMissionTask.class);
        UserMissionTaskResponse response = mock(UserMissionTaskResponse.class);
        when(userRepository.findByUsername(SAMPLE_USERNAME)).thenReturn(Optional.of(user));
        when(missionRepository.findById(SAMPLE_ID)).thenReturn(Optional.of(mission));
        when(userMissionRepository.findByUserAndMission(user, mission)).thenReturn(Optional.of(userMission));
        when(userMissionTaskRepository.findByUserMission(userMission)).thenReturn(List.of(userMissionTask));
        when(missionTaskMapper.toUserMissionTaskResponse(userMissionTask)).thenReturn(response);
        List<UserMissionTaskResponse> result = missionTaskService.findUserMissionTasksByUsernameAndMissionId(SAMPLE_USERNAME, SAMPLE_ID);
        assertThat(result).containsExactly(response);
    }

    @Test
    @DisplayName("유저명+미션ID로 유저 미션 태스크 목록 조회 - 유저 없음")
    void findUserMissionTasksByUsernameAndMissionId_UserNotFound() {
        when(userRepository.findByUsername(SAMPLE_USERNAME)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> missionTaskService.findUserMissionTasksByUsernameAndMissionId(SAMPLE_USERNAME, SAMPLE_ID))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("미션 태스크 추가 - 성공")
    void addMissionTask_Success() {
        MissionTaskRequest request = MissionTaskRequest.builder().name("task").build();
        MissionTask entity = mock(MissionTask.class);
        Mission mission = mock(Mission.class);
        MissionTask saved = mock(MissionTask.class);
        MissionTaskResponse response = mock(MissionTaskResponse.class);
        when(missionTaskMapper.toEntity(request)).thenReturn(entity);
        when(missionRepository.findById(SAMPLE_ID)).thenReturn(Optional.of(mission));
        when(missionTaskRepository.save(entity)).thenReturn(saved);
        when(missionTaskMapper.toMissionTaskResponse(saved)).thenReturn(response);
        MissionTaskResponse result = missionTaskService.addMissionTask(SAMPLE_ID, request);
        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("미션 태스크 추가 - 미션 없음")
    void addMissionTask_MissionNotFound() {
        MissionTaskRequest request = MissionTaskRequest.builder().name("task").build();
        when(missionRepository.findById(SAMPLE_ID)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> missionTaskService.addMissionTask(SAMPLE_ID, request))
                .isInstanceOf(MissionNotFoundException.class);
    }

    @Test
    @DisplayName("미션 태스크 삭제 - 성공")
    void deleteMissionTask_Success() {
        when(missionTaskRepository.existsById(SAMPLE_ID)).thenReturn(true);
        missionTaskService.deleteMissionTask(SAMPLE_ID);
        verify(missionTaskRepository).deleteById(SAMPLE_ID);
    }

    @Test
    @DisplayName("미션 태스크 삭제 - 태스크 없음")
    void deleteMissionTask_NotFound() {
        when(missionTaskRepository.existsById(SAMPLE_ID)).thenReturn(false);
        assertThatThrownBy(() -> missionTaskService.deleteMissionTask(SAMPLE_ID))
                .isInstanceOf(MissionTaskNotFoundException.class);
    }

    @Test
    @DisplayName("미션 태스크 수정 - 성공")
    void updateMissionTask_Success() {
        MissionTaskRequest request = MissionTaskRequest.builder().name("task").score(10).tip("tip").build();
        MissionTask entity = mock(MissionTask.class);
        when(missionTaskRepository.findById(SAMPLE_ID)).thenReturn(Optional.of(entity));
        missionTaskService.updateMissionTask(SAMPLE_ID, request);
        verify(entity).setName("task");
        verify(entity).setScore(10);
        verify(entity).setTip("tip");
    }

    @Test
    @DisplayName("미션 태스크 수정 - 태스크 없음")
    void updateMissionTask_NotFound() {
        MissionTaskRequest request = MissionTaskRequest.builder().name("task").build();
        when(missionTaskRepository.findById(SAMPLE_ID)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> missionTaskService.updateMissionTask(SAMPLE_ID, request))
                .isInstanceOf(MissionTaskNotFoundException.class);
    }

    @Test
    @DisplayName("유저 미션 태스크 상태 변경 - 성공(신규)")
    void changeUserMissionTaskState_New() {
        User user = mock(User.class);
        MissionTask missionTask = mock(MissionTask.class);
        Mission mission = mock(Mission.class);
        UserMission userMission = mock(UserMission.class);
        when(userRepository.findByUsername(SAMPLE_USERNAME)).thenReturn(Optional.of(user));
        when(missionTaskRepository.findById(SAMPLE_ID)).thenReturn(Optional.of(missionTask));
        when(missionTask.getMission()).thenReturn(mission);
        when(userMissionRepository.findByUserAndMission(user, mission)).thenReturn(Optional.of(userMission));
        when(userMissionTaskRepository.findByUserMission_UserAndMissionTask(user, missionTask)).thenReturn(Optional.empty());
        missionTaskService.changeUserMissionTaskState(SAMPLE_USERNAME, SAMPLE_ID, TaskState.COMPLETED);
        verify(userMissionTaskRepository).save(any(UserMissionTask.class));
    }

    @Test
    @DisplayName("유저 미션 태스크 상태 변경 - 성공(기존)")
    void changeUserMissionTaskState_Existing() {
        User user = mock(User.class);
        MissionTask missionTask = mock(MissionTask.class);
        Mission mission = mock(Mission.class);
        UserMission userMission = mock(UserMission.class);
        UserMissionTask userMissionTask = mock(UserMissionTask.class);
        when(userRepository.findByUsername(SAMPLE_USERNAME)).thenReturn(Optional.of(user));
        when(missionTaskRepository.findById(SAMPLE_ID)).thenReturn(Optional.of(missionTask));
        when(missionTask.getMission()).thenReturn(mission);
        when(userMissionRepository.findByUserAndMission(user, mission)).thenReturn(Optional.of(userMission));
        when(userMissionTaskRepository.findByUserMission_UserAndMissionTask(user, missionTask)).thenReturn(Optional.of(userMissionTask));
        missionTaskService.changeUserMissionTaskState(SAMPLE_USERNAME, SAMPLE_ID, TaskState.COMPLETED);
        verify(userMissionTask).setState(TaskState.COMPLETED);
    }

    @Test
    @DisplayName("유저 미션 태스크 상태 변경 - 유저 없음")
    void changeUserMissionTaskState_UserNotFound() {
        when(userRepository.findByUsername(SAMPLE_USERNAME)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> missionTaskService.changeUserMissionTaskState(SAMPLE_USERNAME, SAMPLE_ID, TaskState.COMPLETED))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("유저 미션 태스크 상태 변경 - 태스크 없음")
    void changeUserMissionTaskState_TaskNotFound() {
        User user = mock(User.class);
        when(userRepository.findByUsername(SAMPLE_USERNAME)).thenReturn(Optional.of(user));
        when(missionTaskRepository.findById(SAMPLE_ID)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> missionTaskService.changeUserMissionTaskState(SAMPLE_USERNAME, SAMPLE_ID, TaskState.COMPLETED))
                .isInstanceOf(MissionTaskNotFoundException.class);
    }

    @Test
    @DisplayName("유저 미션 태스크 상태 변경 - 유저 미션 없음")
    void changeUserMissionTaskState_UserMissionNotFound() {
        User user = mock(User.class);
        MissionTask missionTask = mock(MissionTask.class);
        Mission mission = mock(Mission.class);
        when(userRepository.findByUsername(SAMPLE_USERNAME)).thenReturn(Optional.of(user));
        when(missionTaskRepository.findById(SAMPLE_ID)).thenReturn(Optional.of(missionTask));
        when(missionTask.getMission()).thenReturn(mission);
        when(userMissionRepository.findByUserAndMission(user, mission)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> missionTaskService.changeUserMissionTaskState(SAMPLE_USERNAME, SAMPLE_ID, TaskState.COMPLETED))
                .isInstanceOf(UserMissionNotFoundException.class);
    }
}