package org.example.hugmeexp.domain.missionGroup.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.hugmeexp.domain.mission.dto.response.MissionResponse;
import org.example.hugmeexp.domain.mission.dto.response.UserMissionResponse;
import org.example.hugmeexp.domain.mission.enums.Difficulty;
import org.example.hugmeexp.domain.mission.enums.UserMissionState;
import org.example.hugmeexp.domain.mission.service.MissionService;
import org.example.hugmeexp.domain.missionGroup.dto.request.MissionGroupRequest;
import org.example.hugmeexp.domain.missionGroup.dto.response.MissionGroupResponse;
import org.example.hugmeexp.domain.missionGroup.exception.AlreadyExistsUserMissionGroupException;
import org.example.hugmeexp.domain.missionGroup.exception.MissionGroupNotFoundException;
import org.example.hugmeexp.domain.missionGroup.exception.NotExistsUserMissionGroupException;
import org.example.hugmeexp.domain.missionGroup.exception.UserNotFoundException;
import org.example.hugmeexp.domain.missionGroup.service.MissionGroupService;
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

import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("미션 그룹 컨트롤러 테스트")
class MissionGroupControllerTest {
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Long TEST_ID = 1L;
    private final String BASE_URL = "/api/v1/mission-groups";

    @Mock
    private MissionGroupService missionGroupService;
    
    @Mock
    private MissionService missionService;

    @InjectMocks
    private MissionGroupController missionGroupController;

    private final Long TEST_USER_ID = 5L;
    private final Long TEST_GROUP_ID = 10L;


    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(missionGroupController)
                .setControllerAdvice(new ExceptionController())
                .setCustomArgumentResolvers(
                        new AuthenticationPrincipalArgumentResolver()
                )
                .build();
    }

    @Test
    @DisplayName("모든 미션 그룹 조회 - 성공")
    void getAllMissionGroups_ShouldReturnList() throws Exception {
        // Given
        MissionGroupResponse missionGroupResponse = MissionGroupResponse
                .builder()
                .id(TEST_ID)
                .name("Test Group")
                .teacherUsername("teacher1")
                .build();
        given(missionGroupService.getAllMissionGroups())
                .willReturn(List.of(missionGroupResponse));
        // When & Test
        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(TEST_ID))
                .andExpect(jsonPath("$.data[0].name").value("Test Group"));
    }

    @Test
    @DisplayName("미션 그룹 생성 - 성공")
    void createMissionGroup_ShouldReturnCreated() throws Exception {
        // Given
        MissionGroupRequest request = MissionGroupRequest
                .builder()
                .name("New Group")
                .teacherUsername("teacher1")
                .build();
        MissionGroupResponse response = MissionGroupResponse
                .builder()
                .id(TEST_ID)
                .name("New Group")
                .teacherUsername("teacher1")
                .build();

        given(missionGroupService.createMissionGroup(request))
                .willReturn(response);
        // When && Then
        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(TEST_ID))
                .andExpect(jsonPath("$.data.name").value("New Group"));
    }

    @Test
    @DisplayName("ID로 미션 그룹 조회 - 성공")
    void getMissionGroupById_ShouldReturnOk() throws Exception {
        // Given
        MissionGroupResponse response = MissionGroupResponse
                .builder()
                .id(TEST_ID)
                .name("Test Group")
                .teacherUsername("teacher1")
                .build();
        given(missionGroupService.getMissionById(TEST_ID))
                .willReturn(response);
        // When && Then
        mockMvc.perform(get(BASE_URL + "/{id}", TEST_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(TEST_ID))
                .andExpect(jsonPath("$.data.name").value("Test Group"));
    }

    @Test
    @DisplayName("ID로 미션 그룹 조회 - 실패")
    void getMissionGroupById_ShouldReturnNotFound() throws Exception {
        // Given
        long nonExistentId = 999L;
//        MissionGroupResponse response = MissionGroupResponse
//                .builder()
//                .id(nonExistentId)
//                .name("Test Group")
//                .teacherId(100L)
//                .build();
        given(missionGroupService.getMissionById(nonExistentId))
                .willThrow(new MissionGroupNotFoundException());
        // When && Then
        mockMvc.perform(get(BASE_URL + "/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("미션 그룹 수정 - 성공")
    void updateMissionGroup_ShouldReturnOk() throws Exception {
        // Given
        MissionGroupRequest request = MissionGroupRequest
                .builder()
                .name("Updated Group")
                .teacherUsername("teacher2")
                .build();
        MissionGroupResponse response = MissionGroupResponse
                .builder()
                .id(TEST_ID)
                .name("Updated Group")
                .teacherUsername("teacher2")
                .build();

        given(missionGroupService.updateMissionGroup(eq(TEST_ID), any(MissionGroupRequest.class)))
                .willReturn(response);

        // When & Then
        mockMvc.perform(put(BASE_URL + "/{id}", TEST_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(TEST_ID))
                .andExpect(jsonPath("$.data.name").value("Updated Group"))
                .andExpect(jsonPath("$.data.teacherUsername").value("teacher2"));
    }

    @Test
    @DisplayName("미션 그룹 수정 - 실패")
    void updateMissionGroup_ShouldReturnNotFound() throws Exception {
        // Given
        long nonExistentId = 999L;
        MissionGroupRequest request = MissionGroupRequest
                .builder()
                .name("Updated Group")
                .teacherUsername("teacher2")
                .build();

        given(missionGroupService.updateMissionGroup(nonExistentId, request))
                .willThrow(new MissionGroupNotFoundException());

        // When & Then
        mockMvc.perform(put(BASE_URL + "/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("미션 그룹 삭제 - 성공")
    void deleteMissionGroup_ShouldReturnNoContent() throws Exception {
        // Given
        doNothing().when(missionGroupService).deleteMissionGroup(TEST_ID);

        // When & Then
        mockMvc.perform(delete(BASE_URL + "/{id}", TEST_ID))
                .andExpect(status().isNoContent());

        verify(missionGroupService, times(1)).deleteMissionGroup(TEST_ID);
    }

    @Test
    @DisplayName("미션 그룹 삭제 - 실패")
    void deleteMissionGroup_ShouldReturnNotFound() throws Exception {
        // Given
        long nonExistentId = 999L;
        doThrow(new MissionGroupNotFoundException()).when(missionGroupService).deleteMissionGroup(nonExistentId);

        // When & Then
        mockMvc.perform(delete(BASE_URL + "/{id}", nonExistentId))
                .andExpect(status().isNotFound());

        verify(missionGroupService, times(1)).deleteMissionGroup(nonExistentId);
    }

    @Test
    @DisplayName("미션 그룹 ID로 미션 목록 조회 - 성공")
    void getMissionsByMissionGroupId_ShouldReturnOk() throws Exception {
        // Given
        Long missionGroupId = TEST_ID;
        MissionResponse missionResponse = MissionResponse.builder()
                .id(10L)
                .name("미션1")
                .description("설명")
                .difficulty(Difficulty.valueOf("EASY"))
                .rewardPoint(100)
                .rewardExp(50)
                .order(1)
                .build();
        given(missionService.getMissionsByMissionGroupId(missionGroupId))
                .willReturn(List.of(missionResponse));

        // When & Then
        mockMvc.perform(get(BASE_URL + "/{id}/missions", missionGroupId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(10L))
                .andExpect(jsonPath("$.data[0].name").value("미션1"));
    }

    @Test
    @DisplayName("미션 그룹에 사용자 추가 - 성공")
    void addUserToMissionGroup_Success() throws Exception {
        // Given
        // void를 반환하는 서비스 메소드는 doNothing()으로 모킹합니다.
        doNothing().when(missionGroupService).addUserToMissionGroup(TEST_USER_ID, TEST_GROUP_ID);

        // When & Then
        mockMvc.perform(post(BASE_URL + "/{missionGroupId}/users/{userId}", TEST_GROUP_ID, TEST_USER_ID))
                .andExpect(status().isCreated())
                .andDo(print());

        // missionGroupService의 addUserToMissionGroup이 정확한 인자와 함께 1번 호출되었는지 검증
        verify(missionGroupService).addUserToMissionGroup(TEST_USER_ID, TEST_GROUP_ID);
    }

    @Test
    @DisplayName("미션 그룹에 사용자 추가 - 이미 그룹에 속한 경우 - 실패 (409 Conflict)")
    void addUserToMissionGroup_AlreadyExists_Fail() throws Exception {
        // Given
        // 서비스가 AlreadyExistsUserMissionGroupException 예외를 던지도록 모킹합니다.
        doThrow(new AlreadyExistsUserMissionGroupException())
                .when(missionGroupService).addUserToMissionGroup(TEST_USER_ID, TEST_GROUP_ID);

        // When & Then
        mockMvc.perform(post(BASE_URL + "/{missionGroupId}/users/{userId}", TEST_GROUP_ID, TEST_USER_ID))
                .andExpect(status().isConflict()) // ExceptionController가 409 Conflict로 변환한다고 가정
                .andDo(print());
    }

    @Test
    @DisplayName("미션 그룹에 사용자 추가 - 사용자가 존재하지 않는 경우 - 실패 (404 Not Found)")
    void addUserToMissionGroup_UserNotFound_Fail() throws Exception {
        // Given
        doThrow(new UserNotFoundException())
                .when(missionGroupService).addUserToMissionGroup(TEST_USER_ID, TEST_GROUP_ID);

        // When & Then
        mockMvc.perform(post(BASE_URL + "/{missionGroupId}/users/{userId}", TEST_GROUP_ID, TEST_USER_ID))
                .andExpect(status().isNotFound()) // ExceptionController가 404 Not Found로 변환한다고 가정
                .andDo(print());
    }

    @Test
    @DisplayName("미션 그룹에서 사용자 제거 - 성공")
    void removeUserFromMissionGroup_Success() throws Exception {
        // Given
        doNothing().when(missionGroupService).removeUserFromMissionGroup(TEST_USER_ID, TEST_GROUP_ID);

        // When & Then
        mockMvc.perform(delete(BASE_URL + "/{missionGroupId}/users/{userId}", TEST_GROUP_ID, TEST_USER_ID))
                .andExpect(status().isOk())
                .andDo(print());

        // missionGroupService의 removeUserFromMissionGroup이 정확한 인자와 함께 1번 호출되었는지 검증
        verify(missionGroupService).removeUserFromMissionGroup(TEST_USER_ID, TEST_GROUP_ID);
    }

    @Test
    @DisplayName("미션 그룹에서 사용자 제거 - 그룹에 속하지 않은 사용자 - 실패 (404 Not Found)")
    void removeUserFromMissionGroup_NotAMember_Fail() throws Exception {
        // Given
        // 서비스가 NotExistsUserMissionGroupException 예외를 던지도록 모킹합니다.
        doThrow(new NotExistsUserMissionGroupException())
                .when(missionGroupService).removeUserFromMissionGroup(TEST_USER_ID, TEST_GROUP_ID);

        // When & Then
        mockMvc.perform(delete(BASE_URL + "/{missionGroupId}/users/{userId}", TEST_GROUP_ID, TEST_USER_ID))
                .andExpect(status().isNotFound()) // ExceptionController가 404 Not Found로 변환한다고 가정
                .andDo(print());
    }

    @Test
    @DisplayName("미션 그룹에서 사용자 제거 - 미션 그룹이 존재하지 않는 경우 - 실패 (404 Not Found)")
    void removeUserFromMissionGroup_GroupNotFound_Fail() throws Exception {
        // Given
        doThrow(new MissionGroupNotFoundException())
                .when(missionGroupService).removeUserFromMissionGroup(TEST_USER_ID, TEST_GROUP_ID);

        // When & Then
        mockMvc.perform(delete(BASE_URL + "/{missionGroupId}/users/{userId}", TEST_GROUP_ID, TEST_USER_ID))
                .andExpect(status().isNotFound()) // ExceptionController가 404 Not Found로 변환한다고 가정
                .andDo(print());
    }

    @Test
    @DisplayName("미션 그룹 도전 목록 조회 – 성공")
    void getMissionGroupChallenges_Success() throws Exception {
        // given
        String username = "testUser";
        Long missionGroupId = TEST_GROUP_ID;
        UserMissionResponse challenge = UserMissionResponse.builder()
                .id(42L)
                .progress(UserMissionState.COMPLETED)
                .build();

        given(missionGroupService
                .findUserMissionByUsernameAndMissionGroup(username, missionGroupId))
                .willReturn(List.of(challenge));


        UserDetails userDetails = User.withUsername(username)
                .password("dummy")
                .authorities(new SimpleGrantedAuthority("ROLE_USER"))
                .build();
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails, userDetails.getPassword(), userDetails.getAuthorities());

        SecurityContext ctx = new SecurityContextImpl();
        ctx.setAuthentication(auth);
        SecurityContextHolder.setContext(ctx);

        // when & then
        mockMvc.perform(get(BASE_URL + "/{missionGroupId}/challenges", missionGroupId)
                        .principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(
                        "사용자 " + username +
                                "의 미션 그룹 " + missionGroupId +
                                " 도전 목록을 가져왔습니다."))
                .andExpect(jsonPath("$.data[0].id").value(42))
                .andExpect(jsonPath("$.data[0].progress").value("COMPLETED"))
                .andDo(print());

        verify(missionGroupService)
                .findUserMissionByUsernameAndMissionGroup(username, missionGroupId);

        // 테스트 후 정리
        SecurityContextHolder.clearContext();
    }
}