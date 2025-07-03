package org.example.hugmeexp.domain.mission.service;

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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("미션 서비스 테스트")
public class SubmissionServiceTest {
    private static final String USERNAME = "testUser";
    private static final Long UM_ID = 1L;

    @TempDir
    Path tempDir;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMissionRepository userMissionRepository;
    @Mock
    private UserMissionSubmissionRepository userMissionSubmissionRepository;
    @Mock
    private UserMissionSubmissionMapper userMissionSubmissionMapper;
    @Mock
    private UserMissionStateLogRepository userMissionStateLogRepository;
    @Mock
    private UserService userService;

    @Mock
    private MissionRewardExpLogRepository userMissionRewardExpRepository;
    @Mock
    private MissionRewardPointLogRepository userMissionRewardPointRepository;

    @InjectMocks
    private SubmissionServiceImpl submissionService;


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
            submissionService.submitChallenge(userMissionId, request, file);

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
    @DisplayName("챌린지 제출 파일이 비어있는 경우 - 실패")
    void submitChallenge_FileIsEmpty() {
        // Given
        Long userMissionId = 1L;
        String originalFileName = "test-image.png";
        String content = "";

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

            // When & Then
            assertThatThrownBy(() -> submissionService.submitChallenge(userMissionId, request, file),
                    "파일이 비어있거나 존재하지 않습니다.")
                    .isInstanceOf(SubmissionFileUploadException.class);

        }
    }

    @Test
    @DisplayName("챌린지 제출 파일의 확장자가 없는 경우 - 실패")
    void submitChallenge_NoExtension() {
        // Given
        Long userMissionId = 1L;
        String originalFileName = "test-image";
        String content = "123456";

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

            // When & Then
            assertThatThrownBy(() -> submissionService.submitChallenge(userMissionId, request, file),
                    "파일 확장자가 없습니다.")
                    .isInstanceOf(SubmissionFileUploadException.class);

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
        assertThrows(AlreadyExistsUserMissionSubmissionException.class, () -> submissionService.submitChallenge(userMissionId, request, file));

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
        assertThrows(UserMissionNotFoundException.class, () -> submissionService.submitChallenge(userMissionId, request, file));
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
            assertThrows(SubmissionFileUploadException.class, () -> submissionService.submitChallenge(userMissionId, request, mockFile));

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
        SubmissionResponse actualResponse = submissionService.getSubmissionByMissionId(userMissionId);

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
        assertThrows(SubmissionNotFoundException.class, () -> submissionService.getSubmissionByMissionId(userMissionId));
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
        submissionService.updateSubmissionFeedback(userMissionId, request);

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
        assertThrows(SubmissionNotFoundException.class, () -> submissionService.updateSubmissionFeedback(userMissionId, request));

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
        submissionService.receiveReward(UM_ID, USERNAME);

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
                () -> submissionService.receiveReward(UM_ID, USERNAME));

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
                () -> submissionService.receiveReward(UM_ID, USERNAME));

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
                () -> submissionService.receiveReward(UM_ID, USERNAME));

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
                () -> submissionService.receiveReward(UM_ID, USERNAME));

        verify(userMissionRepository, never()).save(any());
        verify(userRepository, never()).save(any());
    }
}
