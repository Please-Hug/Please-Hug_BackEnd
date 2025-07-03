package org.example.hugmeexp.domain.mission.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.hugmeexp.domain.mission.dto.request.SubmissionFeedbackRequest;
import org.example.hugmeexp.domain.mission.dto.response.SubmissionResponse;
import org.example.hugmeexp.domain.mission.dto.response.UserMissionResponse;
import org.example.hugmeexp.domain.mission.exception.AlreadyReceivedRewardException;
import org.example.hugmeexp.domain.mission.exception.InvalidUserMissionStateException;
import org.example.hugmeexp.domain.mission.exception.SubmissionNotFoundException;
import org.example.hugmeexp.domain.mission.exception.UserMissionNotFoundException;
import org.example.hugmeexp.domain.mission.service.SubmissionService;
import org.example.hugmeexp.domain.mission.util.FileUploadUtils;
import org.example.hugmeexp.global.common.exception.ExceptionController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ContentDisposition;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class SubmissionControllerTest {

    private static final String BASE_URL = "/api/v1/submissions";

    @InjectMocks
    private SubmissionController submissionController;

    @Mock
    private SubmissionService submissionService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(submissionController)
                .setControllerAdvice(new ExceptionController())
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();
    }

    // given
    Long userMissionId = 1L;

    UserMissionResponse userMissionResponse = mock(UserMissionResponse.class);

    SubmissionResponse submissionResponse = SubmissionResponse.builder()
            .id(userMissionId)
            .comment("comment")
            .feedback("feedback")
            .fileName("fileName.txt")
            .originalFileName("originalFileName.txt")
            .userMission(userMissionResponse)
            .build();

    @Test
    @DisplayName("제출 정보를 조회한다 - 성공")
    public void getSubmissionByMissionId_Success() throws Exception {



        when(submissionService.getSubmissionByMissionId(any()))
                .thenReturn(submissionResponse);

        // when
        mockMvc.perform(get(BASE_URL + "/{userMissionId}", userMissionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpectAll(
                        jsonPath("$.data.id").value(userMissionId),
                        jsonPath("$.data.comment").value("comment"),
                        jsonPath("$.data.feedback").value("feedback"),
                        jsonPath("$.data.fileName").value("fileName.txt"),
                        jsonPath("$.data.originalFileName").value("originalFileName.txt"),
                        jsonPath("$.data.userMission").exists()
                );

        // then
        verify(submissionService, times(1)).getSubmissionByMissionId(userMissionId);
    }

    @Test
    @DisplayName("유저미션(챌린지)가 없으면 404 Not Found - 실패")
    public void getSubmissionByMissionId_UserMissionNotFound() throws Exception {
        // given
        Long userMissionId = 2L;

        when(submissionService.getSubmissionByMissionId(any()))
                .thenThrow(new SubmissionNotFoundException());

        // when & then
        mockMvc.perform(get(BASE_URL + "/{userMissionId}", userMissionId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("제출 정보를 찾을 수 없습니다."));

        // then
        verify(submissionService, times(1)).getSubmissionByMissionId(userMissionId);
    }

    @Test
    @DisplayName("제출 정보가 없으면 404 Not Found - 실패")
    public void getSubmissionByMissionId_NotFound() throws Exception {
        // given
        Long userMissionId = 9L;

        when(submissionService.getSubmissionByMissionId(any()))
                .thenThrow(new SubmissionNotFoundException());

        // when & then
        mockMvc.perform(get(BASE_URL + "/{userMissionId}", userMissionId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("제출 정보를 찾을 수 없습니다."));

        // then
        verify(submissionService, times(1)).getSubmissionByMissionId(userMissionId);
    }

    @Test
    @DisplayName("미션 파일을 다운로드한다 - 성공")
    public void getSubmissionFileByMissionId_Success() throws Exception {
        // given
        when(submissionService.getSubmissionByMissionId(userMissionId))
                .thenReturn(submissionResponse);
        try (MockedStatic<FileUploadUtils> mockedStatic = Mockito.mockStatic(FileUploadUtils.class)) {
            mockedStatic.when(() -> FileUploadUtils.getUploadPath(any()))
                    .thenReturn(tempDir);
            mockedStatic.when(() -> FileUploadUtils.getSafeFileName(eq(submissionResponse.getFileName())))
                    .thenReturn("fileName.txt");
            mockedStatic.when(() -> FileUploadUtils.getSafeFileName(eq(submissionResponse.getOriginalFileName())))
                    .thenReturn("originalFileName.txt");
            // create a dummy file for testing
            File file = tempDir.resolve("fileName.txt").toFile();
            if (!file.exists()) {
                file.createNewFile();
            }
            // write some content to the file
            try (java.io.FileWriter writer = new java.io.FileWriter(file)) {
                writer.write("content of the fileName.txt");
            }
            // set the file to be returned by the service

            ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
                    .filename("originalFileName.txt", StandardCharsets.UTF_8)
                    .build();

            // when
            mockMvc.perform(get(BASE_URL + "/{userMissionId}/file", userMissionId))
                    .andExpect(status().isOk())
                    // expect content type to be application/octet-stream
                    .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                    .andExpect(header()
                            .string("Content-Disposition", contentDisposition.toString())
                    )
                    .andExpect(content().string("content of the fileName.txt"))
            ;

            // then
            verify(submissionService, times(1)).getSubmissionByMissionId(userMissionId);
        }
    }

    @Test
    @DisplayName("미션 파일 다운로드 실패 - 제출 정보가 없을 때 - 실패")
    public void getSubmissionFileByMissionId_NotFound() throws Exception {
        // given
        when(submissionService.getSubmissionByMissionId(userMissionId))
                .thenThrow(new SubmissionNotFoundException());

        // when & then
        mockMvc.perform(get(BASE_URL + "/{userMissionId}/file", userMissionId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("제출 정보를 찾을 수 없습니다."));

        // then
        verify(submissionService, times(1)).getSubmissionByMissionId(userMissionId);
    }

    @Test
    @DisplayName("미션 파일 다운로드 실패 - 파일이 존재하지 않을 때 - 실패")
    public void getSubmissionFileByMissionId_FileNotFound() throws Exception {
        // given
        when(submissionService.getSubmissionByMissionId(userMissionId))
                .thenReturn(submissionResponse);
        try (MockedStatic<FileUploadUtils> mockedStatic = Mockito.mockStatic(FileUploadUtils.class)) {
            mockedStatic.when(() -> FileUploadUtils.getUploadPath(any()))
                    .thenReturn(tempDir);
            mockedStatic.when(() -> FileUploadUtils.getSafeFileName(eq(submissionResponse.getFileName())))
                    .thenReturn("fileName.txt");
            mockedStatic.when(() -> FileUploadUtils.getSafeFileName(eq(submissionResponse.getOriginalFileName())))
                    .thenReturn("originalFileName.txt");
        }

        // when & then
        mockMvc.perform(get(BASE_URL + "/{userMissionId}/file", userMissionId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("제출 파일을 찾을 수 없습니다."));

        // then
        verify(submissionService, times(1)).getSubmissionByMissionId(userMissionId);
    }

    @Test
    @DisplayName("미션 파일 다운로드 실패 - 파일을 읽을 수 없을 때 - 실패")
    public void getSubmissionFileByMissionId_FileReadError() throws Exception {
        // given
        when(submissionService.getSubmissionByMissionId(userMissionId))
                .thenReturn(submissionResponse);
        try (MockedStatic<FileUploadUtils> mockedStatic = Mockito.mockStatic(FileUploadUtils.class)) {
            mockedStatic.when(() -> FileUploadUtils.getUploadPath(any()))
                    .thenReturn(tempDir);
            mockedStatic.when(() -> FileUploadUtils.getSafeFileName(eq(submissionResponse.getFileName())))
                    .thenReturn("fileName.txt");
            mockedStatic.when(() -> FileUploadUtils.getSafeFileName(eq(submissionResponse.getOriginalFileName())))
                    .thenReturn("originalFileName.txt");
        }

        // create a dummy file that cannot be read
        File file = tempDir.resolve("fileName.txt").toFile();
        if (!file.exists()) {
            file.createNewFile();
        }
        file.setReadable(false);

        // when & then
        mockMvc.perform(get(BASE_URL + "/{userMissionId}/file", userMissionId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("제출 파일을 찾을 수 없습니다."));

        // then
        verify(submissionService, times(1)).getSubmissionByMissionId(userMissionId);
    }

    @Test
    @DisplayName("제출 피드백을 업데이트한다 - 성공")
    public void updateSubmissionFeedback_Success() throws Exception {
        // given
        Long userMissionId = 1L;
        String feedback = "Great job!";
        SubmissionFeedbackRequest feedbackRequest = SubmissionFeedbackRequest.builder().feedback(feedback).build();

        doNothing().when(submissionService).updateSubmissionFeedback(anyLong(), eq(feedbackRequest));


        // when
        mockMvc.perform(patch(BASE_URL + "/{userMissionId}/feedback", userMissionId)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(feedback)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("제출 피드백이 성공적으로 업데이트되었습니다."));

        // then
        verify(submissionService, times(1)).updateSubmissionFeedback(userMissionId, feedbackRequest);
    }

    @Test
    @DisplayName("제출 피드백 업데이트 - 제출 정보가 없을 때 - 실패")
    public void updateSubmissionFeedback_NotFound() throws Exception {
        // given
        Long userMissionId = 2L;
        SubmissionFeedbackRequest feedbackRequest = SubmissionFeedbackRequest.builder().feedback("Good job!").build();

        doThrow(new SubmissionNotFoundException()).when(submissionService).updateSubmissionFeedback(anyLong(), eq(feedbackRequest));

        // when & then
        mockMvc.perform(patch(BASE_URL + "/{userMissionId}/feedback", userMissionId)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(feedbackRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("제출 정보를 찾을 수 없습니다."));

        // then
        verify(submissionService, times(1)).updateSubmissionFeedback(userMissionId, feedbackRequest);
    }

    @Test
    @DisplayName("제출 피드백 업데이트 - 잘못된 요청 형식 - 실패")
    public void updateSubmissionFeedback_BadRequest() throws Exception {
        // given
        Long userMissionId = 3L;
        String invalidFeedback = ""; // 빈 문자열은 유효하지 않은 피드백

        // when & then
        mockMvc.perform(patch(BASE_URL + "/{userMissionId}/feedback", userMissionId)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(invalidFeedback)))
                .andExpect(status().isBadRequest());

        // then
        verify(submissionService, never()).updateSubmissionFeedback(anyLong(), any());
    }

    @Test
    @DisplayName("제출 피드백 업데이트 - 유저미션 정보를 찾을 수 없음 - 실패")
    public void updateSubmissionFeedback_UserMissionNotFound() throws Exception {
        // given
        Long userMissionId = 4L;
        SubmissionFeedbackRequest feedbackRequest = SubmissionFeedbackRequest.builder().feedback("Nice work!").build();

        doThrow(new UserMissionNotFoundException()).when(submissionService).updateSubmissionFeedback(anyLong(), eq(feedbackRequest));

        // when & then
        mockMvc.perform(patch(BASE_URL + "/{userMissionId}/feedback", userMissionId)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(feedbackRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("유저 미션을 찾을 수 없습니다."));

        // then
        verify(submissionService, times(1)).updateSubmissionFeedback(userMissionId, feedbackRequest);
    }

    @Test
    @DisplayName("미션 보상을 받는다 - 성공")
    public void receiveReward_Success() throws Exception {
        // given
        Long userMissionId = 1L;
        String username = "testUser";

        doNothing().when(submissionService).receiveReward(anyLong(), eq(username));

        // UserDetails + Authentication 준비
        UserDetails userDetails = User.withUsername(username)
                .password("dummy")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .build();

        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails,
                userDetails.getPassword(),
                userDetails.getAuthorities()
        );

        // SecurityContextHolder 에도 넣어줘야 @AuthenticationPrincipal 바인딩이 된다.
        SecurityContext ctx = new SecurityContextImpl();
        ctx.setAuthentication(auth);
        SecurityContextHolder.setContext(ctx);


        // when
        mockMvc.perform(post(BASE_URL + "/{userMissionId}/reward", userMissionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("보상이 성공적으로 수령되었습니다."));

        // then
        verify(submissionService, times(1)).receiveReward(userMissionId, username);
    }

    @Test
    @DisplayName("미션 보상 받기 - 이미 보상을 수령한 경우 - 실패")
    public void receiveReward_AlreadyReceived() throws Exception {
        // given
        Long userMissionId = 2L;
        String username = "testUser";

        doThrow(new AlreadyReceivedRewardException()).when(submissionService).receiveReward(anyLong(), eq(username));

        // UserDetails + Authentication 준비
        UserDetails userDetails = User.withUsername(username)
                .password("dummy")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .build();

        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails,
                userDetails.getPassword(),
                userDetails.getAuthorities()
        );

        // SecurityContextHolder 에도 넣어줘야 @AuthenticationPrincipal 바인딩이 된다.
        SecurityContext ctx = new SecurityContextImpl();
        ctx.setAuthentication(auth);
        SecurityContextHolder.setContext(ctx);

        // when & then
        mockMvc.perform(post(BASE_URL + "/{userMissionId}/reward", userMissionId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("이미 보상을 수령했습니다."));

        // then
        verify(submissionService, times(1)).receiveReward(userMissionId, username);
    }

    @Test
    @DisplayName("미션 보상 받기 - 피드백이 완료되지 않은 경우 - 실패")
    public void receiveReward_InvalidState() throws Exception {
        // given
        Long userMissionId = 2L;
        String username = "testUser";

        doThrow(new InvalidUserMissionStateException()).when(submissionService).receiveReward(anyLong(), eq(username));

        // UserDetails + Authentication 준비
        UserDetails userDetails = User.withUsername(username)
                .password("dummy")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .build();

        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails,
                userDetails.getPassword(),
                userDetails.getAuthorities()
        );

        // SecurityContextHolder 에도 넣어줘야 @AuthenticationPrincipal 바인딩이 된다.
        SecurityContext ctx = new SecurityContextImpl();
        ctx.setAuthentication(auth);
        SecurityContextHolder.setContext(ctx);

        // when & then
        mockMvc.perform(post(BASE_URL + "/{userMissionId}/reward", userMissionId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("보상을 수령하기 위해서는 피드백이 완료되어야 합니다."));

        // then
        verify(submissionService, times(1)).receiveReward(userMissionId, username);
    }


}