package org.example.hugmeexp.domain.mission.controller;

import org.example.hugmeexp.domain.mission.dto.request.SubmissionUploadRequest;
import org.example.hugmeexp.domain.mission.dto.response.UserMissionResponse;
import org.example.hugmeexp.domain.mission.enums.UserMissionState;
import org.example.hugmeexp.domain.mission.service.SubmissionService;
import org.example.hugmeexp.domain.mission.service.UserMissionService;
import org.example.hugmeexp.global.common.exception.ExceptionController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("챌린지 컨트롤러 테스트")
class ChallengeControllerTest {
    private final String BASE_URL = "/api/v1/challenges";
    private MockMvc mockMvc;

    @InjectMocks
    private ChallengeController challengeController;

    @Mock
    private SubmissionService submissionService;

    @Mock
    private UserMissionService userMissionService;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(challengeController)
                .setControllerAdvice(new ExceptionController())
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();
    }

    @Test
    @DisplayName("챌린지 상태 업데이트 - 성공")
    void updateChallengeState_Success() throws Exception {
        // given
        Long challengeId = 123L;
        UserMissionState newState = UserMissionState.COMPLETED;

        // when & then
        mockMvc.perform(patch(BASE_URL + "/{challengeId}", challengeId)
                        .param("newProgress", newState.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("챌린지 상태가 성공적으로 업데이트되었습니다."))
                // data 필드는 설정하지 않았으므로 null 이거나 missing
                .andExpect(jsonPath("$.data").doesNotExist());

        // service 호출 검증
        verify(userMissionService).changeUserMissionState(challengeId, newState);
    }

    @Test
    @DisplayName("newProgress 파라미터가 잘못되면 400 Bad Request")
    void updateChallengeState_BadRequest_InvalidEnum() throws Exception {
        Long challengeId = 123L;
        // invalid enum value
        mockMvc.perform(patch(BASE_URL + "/{challengeId}", challengeId)
                        .param("newProgress", "INVALID_STATE"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("챌린지 제출 성공 테스트")
    void submitChallenge_Success() throws Exception {
        // given
        Long challengeId = 1L;
        String fileName = "testFileName";
        String comment = "챌린지 제출합니다";

        // 파일 생성
        MockMultipartFile file = new MockMultipartFile(
                "file", // 반드시 @RequestParam("file")의 이름과 일치
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "file-content".getBytes()
        );

        // doNothing mocking
        doNothing().when(submissionService).submitChallenge(anyLong(), any(SubmissionUploadRequest.class), any(MultipartFile.class));

        // when
        ResultActions result = mockMvc.perform(
                multipart(BASE_URL + "/{challengeId}/submissions", challengeId)
                        .file(file)
                        .param("fileName", fileName)
                        .param("comment", comment)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("챌린지 제출이 성공적으로 완료되었습니다."));

        // Service가 한번 호출되었는지 검증
        verify(submissionService, times(1))
                .submitChallenge(eq(challengeId), any(SubmissionUploadRequest.class), any(MultipartFile.class));
    }

    @Test
    @DisplayName("챌린지 제출 실패 테스트 - 파일 누락")
    void submitChallenge_Fail_MissingFile() throws Exception {
        // given
        Long challengeId = 1L;
        String comment = "챌린지 인증합니다!";

        // when
        ResultActions resultActions = mockMvc.perform(
                multipart(BASE_URL + "/{challengeId}/submissions", challengeId)
                        .param("comment", comment)
                        .param("fileName", "testFileName")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        // @RequestParam("file")은 기본적으로 required=true 이므로 400 Bad Request가 발생해야 함
        resultActions.andExpect(status().isBadRequest());

        // 서비스 메소드가 호출되지 않았는지 확인
        verify(submissionService, never()).submitChallenge(any(), any(), any());
    }

    @Test
    @DisplayName("챌린지 제출 실패 테스트 - @Valid 유효성 검사 실패")
    void submitChallenge_Fail_Validation() throws Exception {
        // given
        Long challengeId = 1L;
        String emptyComment = ""; // @NotBlank 위반

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes(StandardCharsets.UTF_8)
        );

        // when
        ResultActions resultActions = mockMvc.perform(
                multipart(BASE_URL + "/{challengeId}/submissions", challengeId)
                        .file(file)
                        .param("comment", emptyComment) // 유효성 검사에 실패할 파라미터
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        // @Valid에 의해 유효성 검사가 실패했으므로 400 Bad Request가 발생해야 함
        resultActions.andExpect(status().isBadRequest());

        // 서비스 메소드가 호출되지 않았는지 확인
        verify(submissionService, never()).submitChallenge(any(), any(), any());
    }

    @Test
    @DisplayName("모든 챌린지 조회 성공 테스트")
    void getAllChallenges_Success() throws Exception {
        //given

        UserDetails userDetails = User.withUsername("testUser")
                .password("dummy")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_LECTURER")))
                .build();

        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails,
                userDetails.getPassword(),
                userDetails.getAuthorities()
        );

        SecurityContext ctx = new SecurityContextImpl();
        ctx.setAuthentication(auth);
        SecurityContextHolder.setContext(ctx);

        when(userMissionService.getAllUserMissionsByTeacher(eq("testUser")))
                .thenReturn(Collections.emptyList());

        //when
        ResultActions resultActions = mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get(BASE_URL)
                        .principal(auth)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("모든 챌린지를 성공적으로 가져왔습니다."))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());

        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("챌린지 ID로 챌린지 조회 성공 테스트")
    void getChallengeById_Success() throws Exception {
        //given
        when(userMissionService.getUserMissionByChallengeId(anyLong()))
                .thenReturn(mock(UserMissionResponse.class));

        //when
        ResultActions resultActions = mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get(BASE_URL + "/{challengeId}", 1L)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("챌린지 1를 성공적으로 가져왔습니다."))
                .andExpect(jsonPath("$.data").isMap())
                .andDo(print());
    }
}