package org.example.hugmeexp.domain.missionGroup.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.hugmeexp.domain.missionGroup.dto.request.MissionGroupRequest;
import org.example.hugmeexp.domain.missionGroup.dto.response.MissionGroupResponse;
import org.example.hugmeexp.domain.missionGroup.exception.MissionGroupNotFoundException;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @InjectMocks
    private MissionGroupController missionGroupController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(missionGroupController)
                .setControllerAdvice(new ExceptionController())
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
}