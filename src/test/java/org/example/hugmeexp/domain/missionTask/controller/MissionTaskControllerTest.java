package org.example.hugmeexp.domain.missionTask.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.hugmeexp.domain.missionTask.dto.request.MissionTaskRequest;
import org.example.hugmeexp.domain.missionTask.enums.TaskState;
import org.example.hugmeexp.domain.missionTask.exception.MissionTaskNotFoundException;
import org.example.hugmeexp.domain.missionTask.service.MissionTaskService;
import org.example.hugmeexp.global.common.exception.ExceptionController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("미션 태스크 컨트롤러 테스트")
class MissionTaskControllerTest {
    private final String BASE_URL = "/api/v1/mission-tasks";
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private MissionTaskService missionTaskService;
    @InjectMocks
    private MissionTaskController missionTaskController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(missionTaskController)
                .setControllerAdvice(new ExceptionController())
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();
    }

    @Test
    @DisplayName("미션 태스크 삭제 - 성공")
    void deleteMissionTask_Success() throws Exception {
        // given
        Long missionTaskId = 1L;
        doNothing().when(missionTaskService).deleteMissionTask(missionTaskId);

        // when & then
        mockMvc.perform(delete(BASE_URL + "/" + missionTaskId))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("미션 태스크 삭제 - 실패(존재하지 않음)")
    void deleteMissionTask_NotFound() throws Exception {
        // given
        Long missionTaskId = 1L;
        doThrow(new MissionTaskNotFoundException()).when(missionTaskService).deleteMissionTask(missionTaskId);

        // when & then
        mockMvc.perform(delete(BASE_URL + "/" + missionTaskId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("미션 태스크 수정 - 성공")
    void updateMissionTask_Success() throws Exception {
        // given
        Long missionTaskId = 1L;
        MissionTaskRequest request = MissionTaskRequest.builder().name("task").score(1).tip("tip").build();
        doNothing().when(missionTaskService).updateMissionTask(eq(missionTaskId), any(MissionTaskRequest.class));

        // when & then
        mockMvc.perform(put(BASE_URL + "/" + missionTaskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("미션 태스크를 성공적으로 업데이트했습니다."))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    @DisplayName("미션 태스크 상태 변경 - 성공")
    void updateMissionTaskState_Success() throws Exception {
        // given
        Long missionTaskId = 1L;
        TaskState taskState = TaskState.COMPLETED;
        String username = "user";
        UserDetails userDetails = User.withUsername(username)
                .password("dummy")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .build();

        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails,
                userDetails.getPassword(),
                userDetails.getAuthorities()
        );

        SecurityContext ctx = new SecurityContextImpl();
        ctx.setAuthentication(auth);
        SecurityContextHolder.setContext(ctx);

        doNothing().when(missionTaskService).changeUserMissionTaskState(eq(username), eq(missionTaskId), eq(taskState));

        // when & then
        mockMvc.perform(post(BASE_URL + "/" + missionTaskId + "/" + taskState)
                        .principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("미션 태스크 상태를 성공적으로 업데이트했습니다."))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    @DisplayName("미션 태스크 상태 변경 - 실패(미션 태스크 없음)")
    void updateMissionTaskState_NotFound() throws Exception {
        // given
        Long missionTaskId = 1L;
        TaskState taskState = TaskState.COMPLETED;
        String username = "user";
        UserDetails userDetails = User.withUsername(username)
                .password("dummy")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .build();

        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails,
                userDetails.getPassword(),
                userDetails.getAuthorities()
        );

        SecurityContext ctx = new SecurityContextImpl();
        ctx.setAuthentication(auth);
        SecurityContextHolder.setContext(ctx);

        doThrow(new MissionTaskNotFoundException()).when(missionTaskService).changeUserMissionTaskState(eq(username), eq(missionTaskId), eq(taskState));

        // when & then
        mockMvc.perform(post(BASE_URL + "/" + missionTaskId + "/" + taskState)
                        .principal(auth))
                .andExpect(status().isNotFound());
    }
}