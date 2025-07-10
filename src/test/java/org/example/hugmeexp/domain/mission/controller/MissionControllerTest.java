package org.example.hugmeexp.domain.mission.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.hugmeexp.domain.mission.dto.request.MissionRequest;
import org.example.hugmeexp.domain.mission.dto.response.MissionResponse;
import org.example.hugmeexp.domain.mission.dto.response.UserMissionResponse;
import org.example.hugmeexp.domain.mission.enums.Difficulty;
import org.example.hugmeexp.domain.mission.enums.UserMissionState;
import org.example.hugmeexp.domain.mission.exception.MissionNotFoundException;
import org.example.hugmeexp.domain.mission.service.MissionService;
import org.example.hugmeexp.domain.mission.service.UserMissionService;
import org.example.hugmeexp.domain.missionGroup.dto.response.MissionGroupResponse;
import org.example.hugmeexp.domain.missionTask.dto.request.MissionTaskRequest;
import org.example.hugmeexp.domain.missionTask.dto.response.MissionTaskResponse;
import org.example.hugmeexp.domain.missionTask.dto.response.UserMissionTaskResponse;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("미션 컨트롤러 테스트")
class MissionControllerTest {
    private final String BASE_URL = "/api/v1/missions";
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private MissionService missionService;

    @Mock
    private UserMissionService userMissionService;

    @Mock
    private MissionTaskService missionTaskService;

    @InjectMocks
    private MissionController missionController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(missionController)
                .setControllerAdvice(new ExceptionController())
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();
    }

    @Test
    @DisplayName("모든 미션 조회 - 성공")
    void getAllMissions_Success() throws Exception {
        // given
        given(missionService.getAllMissions()).willReturn(Collections.emptyList());

        // when & then
        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("미션 목록을 성공적으로 가져왔습니다."))
                .andExpect(jsonPath("$.data").isArray());

        verify(missionService).getAllMissions();
    }

    @Test
    @DisplayName("미션 생성 - 성공")
    void createMission_Success() throws Exception {
        // given
        MissionRequest request = MissionRequest.builder()
                .missionGroupId(1L)
                .name("Test Mission")
                .description("This is a test mission.")
                .difficulty(Difficulty.valueOf("EASY"))
                .rewardPoint(100)
                .rewardExp(100)
                .order(1)
                .build();
        MissionResponse response = mock(MissionResponse.class);
        given(missionService.createMission(any())).willReturn(response);

        // when & then
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(missionService).createMission(any());
    }

    @Test
    @DisplayName("ID로 미션 조회 - 성공")
    void getMissionById_Success() throws Exception {
        // given
        Long missionId = 1L;

        MissionResponse response = MissionResponse.builder()
                .id(missionId)
                .build();
        given(missionService.getMissionById(eq(missionId))).willReturn(response);

        // when & then
        mockMvc.perform(get(BASE_URL + "/{id}", missionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("미션 1를 가져왔습니다."))
                .andExpect(jsonPath("$.data").exists());

        verify(missionService).getMissionById(eq(missionId));
    }

    @Test
    @DisplayName("ID로 미션 조회 - 실패")
    void getMissionById_MissionNotFound() throws Exception {
        // given
        Long nonExistentId = 999L;
        given(missionService.getMissionById(nonExistentId))
                .willThrow(new MissionNotFoundException());

        // when & then
        mockMvc.perform(get(BASE_URL + "/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("미션 업데이트 - 성공")
    void updateMission_Success() throws Exception {
        // given
        Long missionId = 1L;
        MissionRequest request = MissionRequest.builder()
                .missionGroupId(1L)
                .name("Test Mission")
                .description("This is a test mission.")
                .difficulty(Difficulty.valueOf("EASY"))
                .rewardPoint(100)
                .rewardExp(100)
                .order(1)
                .build();
        MissionResponse response = MissionResponse.builder()
                .id(missionId)
                .build();
        given(missionService.updateMission(eq(missionId), any())).willReturn(response);

        // when & then
        mockMvc.perform(put(BASE_URL + "/{id}", missionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("미션 1를 업데이트 하였습니다."))
                .andExpect(jsonPath("$.data").exists());

        verify(missionService).updateMission(eq(missionId), any());
    }

    @Test
    @DisplayName("미션 업데이트 - 실패")
    void updateMission_MissionNotFound() throws Exception {
        // given
        Long nonExistentId = 999L;
        MissionRequest request = MissionRequest.builder()
                .missionGroupId(1L)
                .name("Test Mission")
                .description("This is a test mission.")
                .difficulty(Difficulty.valueOf("EASY"))
                .rewardPoint(100)
                .rewardExp(100)
                .order(1)
                .build();
        given(missionService.updateMission(eq(nonExistentId), any()))
                .willThrow(new MissionNotFoundException());

        // when & then
        mockMvc.perform(put(BASE_URL + "/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("미션 삭제 - 성공")
    void deleteMission_Success() throws Exception {
        // given
        Long missionId = 1L;
        doNothing().when(missionService).deleteMission(missionId);

        // when & then
        mockMvc.perform(delete(BASE_URL + "/{id}", missionId))
                .andExpect(status().isNoContent());

        verify(missionService).deleteMission(missionId);
    }

    @Test
    @DisplayName("미션 삭제 - 실패")
    void deleteMission_MissionNotFound() throws Exception {
        // given
        Long nonExistentId = 999L;
        doThrow(new MissionNotFoundException()).when(missionService).deleteMission(nonExistentId);

        // when & then
        mockMvc.perform(delete(BASE_URL + "/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("미션 그룹 변경 - 성공")
    void changeMissionGroup_Success() throws Exception {
        // given
        Long missionId = 1L;
        Long groupId = 2L;
        MissionGroupResponse group = MissionGroupResponse.builder()
                .id(groupId)
                .name("Test Group")
                .build();
        MissionResponse response = MissionResponse.builder()
                .id(missionId)
                .missionGroup(group)
                .build();
        given(missionService.changeMissionGroup(missionId, groupId)).willReturn(response);

        // when & then
        mockMvc.perform(patch(BASE_URL + "/{id}/group", missionId)
                        .param("missionGroupId", groupId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("미션 1의 미션 그룹을 변경하였습니다."))
                .andExpect(jsonPath("$.data").exists());

        verify(missionService).changeMissionGroup(missionId, groupId);
    }

    @Test
    @DisplayName("미션 그룹 변경 - 실패")
    void changeMissionGroup_MissionNotFound() throws Exception {
        // given
        Long nonExistentId = 999L;
        Long groupId = 2L;
        given(missionService.changeMissionGroup(nonExistentId, groupId))
                .willThrow(new MissionNotFoundException());

        // when & then
        mockMvc.perform(patch(BASE_URL + "/{id}/group", nonExistentId)
                        .param("missionGroupId", groupId.toString()))
                .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("미션 도전 - 성공 (201)")
    void challengeMission_Success() throws Exception {
        // given
        String username = "testUser";
        Long missionId = 42L;

        // Service 가 리턴해 줄 DTO
        UserMissionResponse serviceResponse = UserMissionResponse.builder()
                .id(99L)
                .progress(UserMissionState.NOT_STARTED)
                .build();

        given(userMissionService.challengeMission(username, missionId))
                .willReturn(serviceResponse);

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
        mockMvc.perform(post(BASE_URL + "/{id}/challenges", missionId)
                        .principal(auth)       // request.getUserPrincipal()에도 담아두고
                        .contentType("application/json"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message")
                        .value("미션 " + missionId + "에 도전하였습니다."))
                .andExpect(jsonPath("$.data.id").value(99))
                .andExpect(jsonPath("$.data.progress")
                        .value(UserMissionState.NOT_STARTED.name()));

        // Service 호출 검증
        verify(userMissionService).challengeMission(username, missionId);

        // 테스트 끝난 뒤에는 Context 초기화
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("missionId의 모든 미션 태스크 조회 - 성공")
    void getAllMissionTasksByMissionId() throws Exception {
        // Given
        Long missionId = 1L;
        MissionTaskResponse missionResponse = mock(MissionTaskResponse.class);
        given(missionTaskService.findByMissionId(missionId)).willReturn(Collections.singletonList(missionResponse));

        // When & Then
        mockMvc.perform(get(BASE_URL + "/{id}/tasks", missionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0]").exists());

    }

    @Test
    @DisplayName("내 미션 태스크 조회 - 성공")
    void getMyMissionTasks() throws Exception {
        // Given
        String username = "testUser";
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

        Long missionId = 42L;
        UserMissionTaskResponse taskResponse = mock(UserMissionTaskResponse.class);
        given(missionTaskService.findUserMissionTasksByUsernameAndMissionId(username, missionId))
                .willReturn(Collections.singletonList(taskResponse));

        // When & Then
        mockMvc.perform(get(BASE_URL + "/{id}/my-tasks", missionId)
                        .principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0]").exists());
    }

    @Test
    void addMissionTask() throws Exception {
        // Given
        Long missionId = 1L;
        MissionTaskResponse taskResponse = mock(MissionTaskResponse.class);
        given(missionTaskService.addMissionTask(eq(missionId), any())).willReturn(taskResponse);

        MissionTaskRequest request = MissionTaskRequest.builder()
                .name("test")
                .score(10)
                .tip("test tip")
                .build();

        // When & Then
        mockMvc.perform(post(BASE_URL + "/{id}/tasks", missionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void getChallenge() throws Exception {
        // Given
        String username = "testUser";
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

        Long missionId = 42L;
        UserMissionResponse missionResponse = mock(UserMissionResponse.class);
        given(userMissionService.getUserMission(missionId, username))
                .willReturn(missionResponse);

        // When & Then
        mockMvc.perform(get(BASE_URL + "/{id}/challenges", missionId)
                        .principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.message").value("미션 " + missionId + " 도전 정보를 가져왔습니다."));
    }
}