package org.example.hugmeexp.domain.mission.service;

import org.example.hugmeexp.domain.mission.dto.request.MissionRequest;
import org.example.hugmeexp.domain.mission.dto.request.SubmissionFeedbackRequest;
import org.example.hugmeexp.domain.mission.dto.request.SubmissionUploadRequest;
import org.example.hugmeexp.domain.mission.dto.response.MissionResponse;
import org.example.hugmeexp.domain.mission.dto.response.SubmissionResponse;
import org.example.hugmeexp.domain.mission.dto.response.UserMissionResponse;
import org.example.hugmeexp.domain.mission.entity.*;
import org.example.hugmeexp.domain.mission.enums.Difficulty;
import org.example.hugmeexp.domain.mission.enums.FileUploadType;
import org.example.hugmeexp.domain.mission.enums.UserMissionState;
import org.example.hugmeexp.domain.mission.exception.*;
import org.example.hugmeexp.domain.mission.mapper.MissionMapper;
import org.example.hugmeexp.domain.mission.mapper.UserMissionMapper;
import org.example.hugmeexp.domain.mission.mapper.UserMissionSubmissionMapper;
import org.example.hugmeexp.domain.mission.repository.*;
import org.example.hugmeexp.domain.mission.util.FileUploadUtils;
import org.example.hugmeexp.domain.missionGroup.entity.MissionGroup;
import org.example.hugmeexp.domain.missionGroup.entity.UserMissionGroup;
import org.example.hugmeexp.domain.missionGroup.exception.MissionGroupNotFoundException;
import org.example.hugmeexp.domain.missionGroup.exception.UserMissionGroupNotFoundException;
import org.example.hugmeexp.domain.missionGroup.exception.UserNotFoundException;
import org.example.hugmeexp.domain.missionGroup.repository.MissionGroupRepository;
import org.example.hugmeexp.domain.missionGroup.repository.UserMissionGroupRepository;
import org.example.hugmeexp.domain.user.repository.UserRepository;
import org.example.hugmeexp.domain.user.entity.User;
import org.example.hugmeexp.domain.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("미션 서비스 테스트")
class MissionServiceTest {

    @Mock
    private MissionRepository missionRepository;

    @Mock
    private MissionGroupRepository missionGroupRepository;

    @Mock
    private MissionMapper missionMapper;

    @InjectMocks
    private MissionServiceImpl missionService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMissionGroupRepository userMissionGroupRepository;

    @Mock
    private UserMissionRepository userMissionRepository;

    @Mock
    private UserMissionMapper userMissionMapper;

    @Mock
    private UserMissionSubmissionRepository userMissionSubmissionRepository;

    @Mock
    private UserMissionSubmissionMapper userMissionSubmissionMapper;

    @Mock
    private MissionRewardExpLogRepository userMissionRewardExpRepository;
    @Mock
    private MissionRewardPointLogRepository userMissionRewardPointRepository;
    @Mock
    private UserMissionStateLogRepository userMissionStateLogRepository;

    @Mock
    private UserService userService;


    @TempDir
    Path tempDir;


    private final Long SAMPLE_ID = 1L;
    private final String SAMPLE_NAME = "샘플 미션";

    private final MissionRequest SAMPLE_REQUEST = MissionRequest.builder()
            .name("미션명")
            .description("미션 설명")
            .difficulty(Difficulty.HARD)
            .rewardPoint(100)
            .rewardExp(50)
            .order(1)
            .missionGroupId(SAMPLE_ID)
            .build();

    private static final Long UM_ID     = 1L;
    private static final String USERNAME = "testUser";

    @Test
    @DisplayName("미션을 정상적으로 생성한다 - 성공")
    void createMission_Success() {
        // Given
        MissionGroup group = MissionGroup.builder().id(SAMPLE_ID).build();
        Mission mission = Mission.builder().id(SAMPLE_ID).build();
        MissionResponse expectedResponse = MissionResponse.builder().id(SAMPLE_ID).name(SAMPLE_NAME).build();

        when(missionGroupRepository.findById(SAMPLE_ID)).thenReturn(Optional.of(group));
        when(missionMapper.toEntity(SAMPLE_REQUEST)).thenReturn(mission);
        when(missionRepository.save(any(Mission.class))).thenReturn(mission);
        when(missionMapper.toMissionResponse(mission)).thenReturn(expectedResponse);

        // When
        MissionResponse result = missionService.createMission(SAMPLE_REQUEST);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        verify(missionRepository).save(any(Mission.class));
    }

    @Test
    @DisplayName("존재하지 않는 미션 그룹으로 생성 시도 - 실패")
    void createMission_NonExistingGroup_Fail() {
        // Given
        when(missionGroupRepository.findById(SAMPLE_ID)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> missionService.createMission(SAMPLE_REQUEST))
                .isInstanceOf(MissionGroupNotFoundException.class);
        verify(missionRepository, never()).save(any());
    }

    @Test
    @DisplayName("ID로 미션을 정상적으로 조회한다 - 성공")
    void getMissionById_Success() {
        // Given
        Mission mission = Mission.builder().id(SAMPLE_ID).build();
        MissionResponse expectedResponse = MissionResponse.builder().id(SAMPLE_ID).name(SAMPLE_NAME).build();

        when(missionRepository.findById(SAMPLE_ID)).thenReturn(Optional.of(mission));
        when(missionMapper.toMissionResponse(mission)).thenReturn(expectedResponse);

        // When
        MissionResponse result = missionService.getMissionById(SAMPLE_ID);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("존재하지 않는 ID로 미션 조회 시도 - 실패")
    void getMissionById_NonExistingId_Fail() {
        // Given
        when(missionRepository.findById(SAMPLE_ID)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> missionService.getMissionById(SAMPLE_ID))
                .isInstanceOf(MissionNotFoundException.class);
    }

    @Test
    @DisplayName("모든 미션을 정상적으로 조회한다 - 성공")
    void getAllMissions_Success() {
        // Given
        Mission sampleMission = Mission.builder().id(SAMPLE_ID).build();
        List<Mission> missionList = List.of(sampleMission);
        MissionResponse sampleResponse = MissionResponse.builder().id(SAMPLE_ID).name(SAMPLE_NAME).build();

        when(missionRepository.findAll()).thenReturn(missionList);
        when(missionMapper.toMissionResponse(sampleMission)).thenReturn(sampleResponse);

        // When
        List<MissionResponse> result = missionService.getAllMissions();

        // Then
        assertThat(result)
                .hasSize(1)
                .containsExactly(sampleResponse);
    }

    @Test
    @DisplayName("빈 미션 목록을 정상적으로 조회한다 - 성공")
    void getAllMissions_EmptyList_Success() {
        // Given
        when(missionRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<MissionResponse> result = missionService.getAllMissions();

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("미션 정보를 정상적으로 수정한다 - 성공")
    void updateMission_Success() {
        // Given
        Mission existingMission = Mission.builder()
                .id(SAMPLE_ID)
                .name("기존 이름")
                .build();

        Mission updatedMission = Mission.builder()
                .id(SAMPLE_ID)
                .name(SAMPLE_REQUEST.getName())
                .description(SAMPLE_REQUEST.getDescription())
                .difficulty(SAMPLE_REQUEST.getDifficulty())
                .rewardPoint(SAMPLE_REQUEST.getRewardPoint())
                .rewardExp(SAMPLE_REQUEST.getRewardExp())
                .order(SAMPLE_REQUEST.getOrder())
                .build();

        MissionResponse expectedResponse = MissionResponse.builder().id(SAMPLE_ID).name("미션명").build();

        when(missionRepository.findById(SAMPLE_ID)).thenReturn(Optional.of(existingMission));
        when(missionRepository.save(any(Mission.class))).thenReturn(updatedMission);
        when(missionMapper.toMissionResponse(updatedMission)).thenReturn(expectedResponse);

        // When
        MissionResponse result = missionService.updateMission(SAMPLE_ID, SAMPLE_REQUEST);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        verify(missionRepository).save(argThat(m -> {
            assertThat(m.getName()).isEqualTo("미션명");
            assertThat(m.getDescription()).isEqualTo("미션 설명");
            return true;
        }));
    }

    @Test
    @DisplayName("존재하지 않는 미션 수정 시도 - 실패")
    void updateMission_NonExistingMission_Fail() {
        // Given
        when(missionRepository.findById(SAMPLE_ID)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> missionService.updateMission(SAMPLE_ID, SAMPLE_REQUEST))
                .isInstanceOf(MissionNotFoundException.class);
        verify(missionRepository, never()).save(any());
    }

    @Test
    @DisplayName("미션을 정상적으로 삭제한다 - 성공")
    void deleteMission_Success() {
        // Given
        Mission mission = Mission.builder().id(SAMPLE_ID).build();
        when(missionRepository.findById(SAMPLE_ID)).thenReturn(Optional.of(mission));

        // When
        missionService.deleteMission(SAMPLE_ID);

        // Then
        verify(missionRepository).delete(mission);
    }

    @Test
    @DisplayName("존재하지 않는 미션 삭제 시도 - 실패")
    void deleteMission_NonExistingMission_Fail() {
        // Given
        when(missionRepository.findById(SAMPLE_ID)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> missionService.deleteMission(SAMPLE_ID))
                .isInstanceOf(MissionNotFoundException.class);
        verify(missionRepository, never()).delete(any());
    }

    @Test
    @DisplayName("미션 그룹을 정상적으로 변경한다 - 성공")
    void changeMissionGroup_Success() {
        // Given
        MissionGroup oldGroup = MissionGroup.builder().id(1L).build();
        MissionGroup newGroup = MissionGroup.builder().id(2L).build();

        Mission existingMission = Mission.builder()
                .id(SAMPLE_ID)
                .missionGroup(oldGroup)
                .build();

        Mission updatedMission = Mission.builder()
                .id(SAMPLE_ID)
                .missionGroup(newGroup)
                .build();

        MissionResponse expectedResponse = MissionResponse.builder()
                .id(SAMPLE_ID)
                .name("변경된 미션")
                .build();

        when(missionRepository.findById(SAMPLE_ID)).thenReturn(Optional.of(existingMission));
        when(missionGroupRepository.findById(2L)).thenReturn(Optional.of(newGroup));
        when(missionRepository.save(any(Mission.class))).thenReturn(updatedMission);
        when(missionMapper.toMissionResponse(updatedMission)).thenReturn(expectedResponse);

        // When
        MissionResponse result = missionService.changeMissionGroup(SAMPLE_ID, 2L);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        verify(missionRepository).save(argThat(m -> {
            assertThat(m.getMissionGroup().getId()).isEqualTo(2L);
            return true;
        }));
    }

    @Test
    @DisplayName("존재하지 않는 그룹으로 변경 시도 - 실패")
    void changeMissionGroup_NonExistingGroup_Fail() {
        // Given
        Mission mission = Mission.builder().id(SAMPLE_ID).build();
        when(missionRepository.findById(SAMPLE_ID)).thenReturn(Optional.of(mission));
        when(missionGroupRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> missionService.changeMissionGroup(SAMPLE_ID, 999L))
                .isInstanceOf(MissionGroupNotFoundException.class);
        verify(missionRepository, never()).save(any());
    }

    @Test
    @DisplayName("특정 그룹의 미션들을 정상적으로 조회한다 - 성공")
    void getMissionsByMissionGroupId_Success() {
        // Given
        MissionGroup group = MissionGroup.builder().id(SAMPLE_ID).build();
        Mission mission = Mission.builder().id(SAMPLE_ID).build();
        MissionResponse response = MissionResponse.builder()
                .id(SAMPLE_ID)
                .name(SAMPLE_NAME)
                .build();

        when(missionGroupRepository.findById(SAMPLE_ID)).thenReturn(Optional.of(group));
        when(missionRepository.findMissionByMissionGroup(group)).thenReturn(List.of(mission));
        when(missionMapper.toMissionResponse(mission)).thenReturn(response);

        // When
        List<MissionResponse> result = missionService.getMissionsByMissionGroupId(SAMPLE_ID);

        // Then
        assertThat(result)
                .hasSize(1)
                .containsExactly(response);
    }

    @Test
    @DisplayName("미션이 없는 그룹 조회 시 빈 목록 반환 - 성공")
    void getMissionsByMissionGroupId_EmptyGroup_Success() {
        // Given
        MissionGroup group = MissionGroup.builder().id(SAMPLE_ID).build();
        when(missionGroupRepository.findById(SAMPLE_ID)).thenReturn(Optional.of(group));
        when(missionRepository.findMissionByMissionGroup(group)).thenReturn(Collections.emptyList());

        // When
        List<MissionResponse> result = missionService.getMissionsByMissionGroupId(SAMPLE_ID);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 그룹의 미션 조회 시도 - 실패")
    void getMissionsByMissionGroupId_NonExistingGroup_Fail() {
        // Given
        when(missionGroupRepository.findById(SAMPLE_ID)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> missionService.getMissionsByMissionGroupId(SAMPLE_ID))
                .isInstanceOf(MissionGroupNotFoundException.class);
        verify(missionRepository, never()).findMissionByMissionGroup(any());
    }






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
        UserMissionResponse actual = missionService.challengeMission(username, missionId);

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
                () -> missionService.challengeMission("nope", 123L));
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
                () -> missionService.challengeMission(username, 999L));
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
                () -> missionService.challengeMission(username, 2L));
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
        missionService.changeUserMissionState(userMissionId, UserMissionState.COMPLETED);

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
                () -> missionService.changeUserMissionState(999L, UserMissionState.COMPLETED));
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
                () -> missionService.challengeMission(username, missionId));
    }





    @Test
    @DisplayName("챌린지 제출을 정상적으로 수행한다 - 성공")
    void submitChallenge_Success() {
        // Given
        Long userMissionId = 1L;
        String originalFileName = "test-image.png";
        String content = "file content";

        try (MockedStatic<FileUploadUtils> mockedStatic = Mockito.mockStatic(FileUploadUtils.class)) {
            FileUploadType fileUploadType = FileUploadType.MISSION_UPLOADS;
            mockedStatic.when(() -> FileUploadUtils.getUploadPath(fileUploadType))
                    .thenReturn(tempDir);
            mockedStatic.when(() -> FileUploadUtils.getSafeFileName(originalFileName))
                    .thenReturn(originalFileName);

            UserMission userMission = UserMission.builder().id(userMissionId).build();
            SubmissionUploadRequest request = new SubmissionUploadRequest("제출 제목", "제출 내용");
            MultipartFile file = new MockMultipartFile("file", originalFileName, "image/png", content.getBytes());
            Submission submission = Submission.builder().build();

            when(userMissionRepository.findById(userMissionId)).thenReturn(Optional.of(userMission));
            when(userMissionSubmissionRepository.existsByUserMission(userMission)).thenReturn(false);
            when(userMissionSubmissionMapper.toEntity(request)).thenReturn(submission);

            // When
            missionService.submitChallenge(userMissionId, request, file);

            // Then
            // 1. submission이 저장되었는지 검증
            ArgumentCaptor<Submission> submissionCaptor = ArgumentCaptor.forClass(Submission.class);
            verify(userMissionSubmissionRepository).save(submissionCaptor.capture());

            // 2. 저장된 submission의 필드들이 올바르게 설정되었는지 검증
            Submission savedSubmission = submissionCaptor.getValue();
            assertThat(savedSubmission.getUserMission()).isEqualTo(userMission);
            assertThat(savedSubmission.getOriginalFileName()).isEqualTo(originalFileName);
            // UUID는 예측 불가능하므로 null이 아닌지만 확인
            assertThat(savedSubmission.getFileName()).isNotNull();

            // 3. 실제 파일이 임시 디렉토리에 저장되었는지 검증
            File savedFile = new File(tempDir.toString(), savedSubmission.getFileName());
            assertThat(savedFile).exists();
            assertThat(savedFile).hasContent(content);
        }
    }

    @Test
    @DisplayName("이미 제출한 챌린지에 다시 제출하면 예외가 발생한다 - 실패")
    void submitChallenge_Fail_WhenAlreadyExists() {
        // Given
        Long userMissionId = 1L;
        UserMission userMission = UserMission.builder().id(userMissionId).build();
        SubmissionUploadRequest request = new SubmissionUploadRequest("제목", "내용");
        MultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "content".getBytes());

        when(userMissionRepository.findById(userMissionId)).thenReturn(Optional.of(userMission));
        // 이미 제출물이 존재한다고 설정
        when(userMissionSubmissionRepository.existsByUserMission(userMission)).thenReturn(true);

        // When & Then
        assertThrows(AlreadyExistsUserMissionSubmissionException.class, () -> missionService.submitChallenge(userMissionId, request, file));

        // save 메서드가 호출되지 않았는지 검증
        verify(userMissionSubmissionRepository, never()).save(any(Submission.class));
    }

    @Test
    @DisplayName("존재하지 않는 사용자 미션에 챌린지를 제출하면 예외가 발생한다 - 실패")
    void submitChallenge_Fail_WhenUserMissionNotFound() {
        // Given
        Long userMissionId = 999L; // 존재하지 않는 ID
        SubmissionUploadRequest request = new SubmissionUploadRequest("제목", "내용");
        MultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "content".getBytes());

        when(userMissionRepository.findById(userMissionId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserMissionNotFoundException.class, () -> missionService.submitChallenge(userMissionId, request, file));
    }

    @Test
    @DisplayName("파일 저장 중 IOException 발생 시 SubmissionFileUploadException으로 전환되어 던져진다 - 실패")
    void submitChallenge_Fail_WhenIoExceptionOccurs() throws IOException {

        try (MockedStatic<FileUploadUtils> mockedStatic = Mockito.mockStatic(FileUploadUtils.class)) {
            FileUploadType fileUploadType = FileUploadType.MISSION_UPLOADS;
            mockedStatic.when(() -> FileUploadUtils.getUploadPath(fileUploadType))
                    .thenReturn(tempDir);
            mockedStatic.when(() -> FileUploadUtils.getSafeFileName(any(String.class)))
                    .thenReturn("valid.name.txt");

            // Given
            Long userMissionId = 1L;
            UserMission userMission = UserMission.builder().id(userMissionId).build();
            SubmissionUploadRequest request = new SubmissionUploadRequest("제목", "내용");
            Submission submission = Submission.builder().build();

            // transferTo() 메서드에서 IOException을 던지도록 조작된 Mock MultipartFile
            MultipartFile mockFile = mock(MultipartFile.class);
            when(mockFile.getOriginalFilename()).thenReturn("valid.name.txt");
            doThrow(new IOException("Disk is full")).when(mockFile).transferTo(any(File.class));


            when(userMissionRepository.findById(userMissionId)).thenReturn(Optional.of(userMission));
            when(userMissionSubmissionRepository.existsByUserMission(userMission)).thenReturn(false);
            when(userMissionSubmissionMapper.toEntity(request)).thenReturn(submission);

            // When & Then
            // RuntimeException인 SubmissionFileUploadException이 발생하는지 확인
            assertThrows(SubmissionFileUploadException.class, () -> missionService.submitChallenge(userMissionId, request, mockFile));

            // @Transactional에 의해 롤백되므로 save는 호출되지만 커밋되지 않음.
            // 테스트에서는 save가 호출되었는지 여부만 확인할 수 있음.
            verify(userMissionSubmissionRepository).save(any(Submission.class));
        }
    }


    @Test
    @DisplayName("사용자 미션 ID로 제출물을 정상적으로 조회한다 - 성공")
    void getSubmissionByMissionId_Success() {
        // Given
        Long userMissionId = 1L;
        UserMission userMission = UserMission.builder().id(userMissionId).build();
        Submission submission = Submission.builder().id(10L).comment("제출물").build();
        SubmissionResponse expectedResponse = SubmissionResponse.builder().id(10L).comment("제출물").build();

        when(userMissionRepository.findById(userMissionId)).thenReturn(Optional.of(userMission));
        when(userMissionSubmissionRepository.findByUserMission(userMission)).thenReturn(Optional.of(submission));
        when(userMissionSubmissionMapper.toSubmissionResponse(submission)).thenReturn(expectedResponse);

        // When
        SubmissionResponse actualResponse = missionService.getSubmissionByMissionId(userMissionId);

        // Then
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("제출물이 없는 사용자 미션 ID로 조회 시 예외가 발생한다 - 실패")
    void getSubmissionByMissionId_Fail_WhenSubmissionNotFound() {
        // Given
        Long userMissionId = 1L;
        UserMission userMission = UserMission.builder().id(userMissionId).build();

        when(userMissionRepository.findById(userMissionId)).thenReturn(Optional.of(userMission));
        // 제출물이 없다고 설정
        when(userMissionSubmissionRepository.findByUserMission(userMission)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(SubmissionNotFoundException.class, () -> missionService.getSubmissionByMissionId(userMissionId));
    }

    @Test
    @DisplayName("제출물에 피드백을 정상적으로 업데이트한다 - 성공")
    void updateSubmissionFeedback_Success() {
        // Given
        Long userMissionId = 1L;
        String feedbackContent = "피드백 내용입니다.";
        SubmissionFeedbackRequest request = new SubmissionFeedbackRequest(feedbackContent);

        UserMission userMission = UserMission.builder().id(userMissionId).build();
        Submission submission = Submission.builder().id(10L).feedback("기존 피드백").build();

        when(userMissionRepository.findById(userMissionId)).thenReturn(Optional.of(userMission));
        when(userMissionSubmissionRepository.findByUserMission(userMission)).thenReturn(Optional.of(submission));

        // When
        missionService.updateSubmissionFeedback(userMissionId, request);

        // Then
        // save 메서드로 전달된 submission 객체를 캡처
        ArgumentCaptor<Submission> submissionCaptor = ArgumentCaptor.forClass(Submission.class);
        verify(userMissionSubmissionRepository).save(submissionCaptor.capture());

        // 캡처된 submission 객체의 feedback 필드가 요청된 내용으로 변경되었는지 확인
        Submission savedSubmission = submissionCaptor.getValue();
        assertThat(savedSubmission.getFeedback()).isEqualTo(feedbackContent);
    }

    @Test
    @DisplayName("제출물이 없는 미션에 피드백 업데이트 시 예외가 발생한다 - 실패")
    void updateSubmissionFeedback_Fail_WhenSubmissionNotFound() {
        // Given
        Long userMissionId = 1L;
        String feedbackContent = "피드백";
        SubmissionFeedbackRequest request = new SubmissionFeedbackRequest(feedbackContent);

        UserMission userMission = UserMission.builder().id(userMissionId).build();

        when(userMissionRepository.findById(userMissionId)).thenReturn(Optional.of(userMission));
        when(userMissionSubmissionRepository.findByUserMission(userMission)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(SubmissionNotFoundException.class, () -> missionService.updateSubmissionFeedback(userMissionId, request));

        // save 메서드가 호출되지 않았는지 검증
        verify(userMissionSubmissionRepository, never()).save(any(Submission.class));
    }

    @Test
    @DisplayName("피드백 완료 상태에서 보상을 정상적으로 수령한다 - 성공")
    void receiveReward_Success() {
        // Given
        User user = User.builder()
                .username(USERNAME)
                .build();
        Mission mission = Mission.builder()
                .rewardPoint(100)
                .rewardExp(50)
                .build();
        UserMission um = UserMission.builder()
                .id(UM_ID)
                .mission(mission)
                .progress(UserMissionState.FEEDBACK_COMPLETED)
                .build();

        when(userRepository.findByUsername(USERNAME))
                .thenReturn(Optional.of(user));
        when(userMissionRepository.findById(UM_ID))
                .thenReturn(Optional.of(um));
        when(userMissionRewardExpRepository.save(any()))
                .thenReturn(mock(MissionRewardExpLog.class));
        when(userMissionRewardPointRepository.save(any()))
                .thenReturn(mock(MissionRewardPointLog.class));
        when(userMissionStateLogRepository.save(any()))
                .thenReturn(mock(UserMissionStateLog.class));
        doAnswer(invocation -> {
            User u = invocation.getArgument(0);
            Integer exp = invocation.getArgument(1);
            u.increaseExp(exp);
            return null;
        }).when(userService)
                .increaseExp(eq(user), eq(50));
        doAnswer(invocation -> {
            User u = invocation.getArgument(0);
            Integer point = invocation.getArgument(1);
            u.increasePoint(point);
            return null;
        }).when(userService)
                .increasePoint(eq(user), eq(100));
        // When
        missionService.receiveReward(UM_ID, USERNAME);

        // Then
        // 1) 상태 변경
        assertEquals(UserMissionState.REWARD_RECEIVED, um.getProgress());
        // 2) 포인트·EXP 증가
        assertEquals(100, user.getPoint());
        assertEquals(50,  user.getExp());
    }

    @Test
    @DisplayName("사용자가 없으면 UserNotFoundException 발생 - 실패")
    void receiveReward_Fail_UserNotFound() {
        // Given
        when(userRepository.findByUsername(USERNAME))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class,
                () -> missionService.receiveReward(UM_ID, USERNAME));

        verify(userMissionRepository, never()).findById(any());
        verify(userMissionRepository, never()).save(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("유저 미션이 없으면 UserMissionNotFoundException 발생 - 실패")
    void receiveReward_Fail_UserMissionNotFound() {
        // Given
        User user = User.builder().username(USERNAME).build();
        when(userRepository.findByUsername(USERNAME))
                .thenReturn(Optional.of(user));
        when(userMissionRepository.findById(UM_ID))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserMissionNotFoundException.class,
                () -> missionService.receiveReward(UM_ID, USERNAME));

        verify(userMissionRepository, never()).save(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("이미 보상을 수령한 경우 AlreadyReceivedRewardException 발생 - 실패")
    void receiveReward_Fail_AlreadyReceived() {
        // Given
        User user = User.builder().username(USERNAME).build();
        Mission mission = Mission.builder().rewardPoint(10).rewardExp(5).build();
        UserMission um = UserMission.builder()
                .id(UM_ID)
                .mission(mission)
                .progress(UserMissionState.REWARD_RECEIVED)
                .build();

        when(userRepository.findByUsername(USERNAME))
                .thenReturn(Optional.of(user));
        when(userMissionRepository.findById(UM_ID))
                .thenReturn(Optional.of(um));

        // When & Then
        assertThrows(AlreadyReceivedRewardException.class,
                () -> missionService.receiveReward(UM_ID, USERNAME));

        verify(userMissionRepository, never()).save(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("피드백 완료 상태가 아니면 InvalidUserMissionStateException 발생 - 실패")
    void receiveReward_Fail_InvalidState() {
        // Given
        User user = User.builder().username(USERNAME).build();
        Mission mission = Mission.builder().rewardPoint(10).rewardExp(5).build();
        UserMission um = UserMission.builder()
                .id(UM_ID)
                .mission(mission)
                .progress(UserMissionState.NOT_STARTED)  // 잘못된 상태
                .build();

        when(userRepository.findByUsername(USERNAME))
                .thenReturn(Optional.of(user));
        when(userMissionRepository.findById(UM_ID))
                .thenReturn(Optional.of(um));

        // When & Then
        assertThrows(InvalidUserMissionStateException.class,
                () -> missionService.receiveReward(UM_ID, USERNAME));

        verify(userMissionRepository, never()).save(any());
        verify(userRepository, never()).save(any());
    }
}