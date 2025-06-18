package org.example.hugmeexp.domain.mission.service;

import org.example.hugmeexp.domain.mission.dto.request.MissionGroupRequest;
import org.example.hugmeexp.domain.mission.dto.response.MissionGroupResponse;
import org.example.hugmeexp.domain.mission.entity.MissionGroup;
import org.example.hugmeexp.domain.mission.exception.MissionGroupNotFoundException;
import org.example.hugmeexp.domain.mission.mapper.MissionGroupMapper;
import org.example.hugmeexp.domain.mission.repository.MissionGroupRepository;
import org.example.hugmeexp.global.common.repository.UserRepository;
import org.example.hugmeexp.global.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("미션 그룹 서비스 테스트")
class MissionGroupServiceTest {

    @Mock
    private MissionGroupRepository missionGroupRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MissionGroupMapper missionGroupMapper;

    @InjectMocks
    private MissionGroupServiceImpl missionGroupService;

    @Test
    @DisplayName("모든 미션 그룹을 조회한다 - 성공")
    void getAllMissionGroups() {
        // Given
        MissionGroup group1 = mock(MissionGroup.class);
        MissionGroup group2 = mock(MissionGroup.class);
        MissionGroupResponse response1 = MissionGroupResponse
                .builder()
                .id(1L)
                .name("Group1")
                .teacherUsername("teacher1")
                .build();
        MissionGroupResponse response2 = MissionGroupResponse
                .builder()
                .id(2L)
                .name("Group2")
                .teacherUsername("teacher2")
                .build();
        when(missionGroupRepository.findAll()).thenReturn(List.of(group1, group2));
        when(missionGroupMapper.toMissionGroupResponse(group1)).thenReturn(response1);
        when(missionGroupMapper.toMissionGroupResponse(group2)).thenReturn(response2);

        // When
        List<MissionGroupResponse> result = missionGroupService.getAllMissionGroups();

        // Then
        assertEquals(2, result.size());
        assertEquals("Group1", result.get(0).getName());
        assertEquals("Group2", result.get(1).getName());
        verify(missionGroupRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("새로운 미션 그룹을 생성한다 - 성공")
    void createMissionGroup() {
        // Given
        User user1 = User.createUser("teacher1", "password", "Teacher One", "1234");

        MissionGroupRequest request = MissionGroupRequest
                .builder()
                .name("New Group")
                .teacherUsername("teacher1")
                .build();
        MissionGroup savedGroup = MissionGroup.builder()
                .id(3L)
                .name("New Group")
                .teacher(user1)
                .build();
        MissionGroupResponse expectedResponse = MissionGroupResponse.builder()
                .id(3L)
                .name("New Group")
                .teacherUsername("teacher1")
                .build();

        when(missionGroupRepository.save(any(MissionGroup.class))).thenReturn(savedGroup);
        when(missionGroupMapper.toMissionGroupResponse(savedGroup)).thenReturn(expectedResponse);
        when(userRepository.findByUsername("teacher1")).thenReturn(Optional.of(user1));
        // When
        MissionGroupResponse result = missionGroupService.createMissionGroup(request);

        // Then
        assertEquals("New Group", result.getName());
        assertEquals("teacher1", result.getTeacherUsername());
        verify(missionGroupRepository, times(1)).save(any(MissionGroup.class));
    }

    @Test
    @DisplayName("ID로 미션 그룹을 조회한다 - 존재O")
    void getMissionById_found() {
        // Given
        Long id = 1L;
        MissionGroup group = mock(MissionGroup.class);
        MissionGroupResponse expectedResponse = MissionGroupResponse
                .builder()
                .id(id)
                .name("Existing Group")
                .teacherUsername("teacher1")
                .build();

        when(missionGroupRepository.findById(id)).thenReturn(Optional.of(group));
        when(missionGroupMapper.toMissionGroupResponse(group)).thenReturn(expectedResponse);

        // When
        MissionGroupResponse result = missionGroupService.getMissionById(id);

        // Then
        assertNotNull(result);
        assertEquals("Existing Group", result.getName());
    }

    @Test
    @DisplayName("ID로 미션 그룹을 조회한다 - 존재X")
    void getMissionById_notFound() {
        // Given
        Long id = 999L;
        when(missionGroupRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        MissionGroupNotFoundException exception = assertThrows(MissionGroupNotFoundException.class,
                () -> missionGroupService.getMissionById(id));

        assertNotNull(exception);
    }

    @Test
    @DisplayName("미션 그룹을 업데이트한다 - 성공")
    void updateMissionGroup_success() {
        Long id = 1L;
        // Given
        User teacher = User.createUser("teacher", "1234", "teacher", "1234");

        MissionGroupRequest request = MissionGroupRequest
                .builder()
                .name("Updated Group")
                .teacherUsername("teacher")
                .build();

        MissionGroup existingGroup = MissionGroup.builder()
                .id(id)
                .teacher(teacher)
                .name("Original Group")
                .build();

        MissionGroupResponse expectedResponse = MissionGroupResponse
                .builder()
                .id(id)
                .name("Updated Group")
                .teacherUsername("teacher")
                .build();

        when(missionGroupRepository.findById(id)).thenReturn(Optional.of(existingGroup));
        when(missionGroupRepository.save(any())).thenReturn(existingGroup);
        when(missionGroupMapper.toMissionGroupResponse(any())).thenReturn(expectedResponse);
        when(userRepository.findByUsername("teacher")).thenReturn(Optional.of(teacher));

        // When
        MissionGroupResponse result = missionGroupService.updateMissionGroup(id, request);

        // Then
        assertEquals("Updated Group", result.getName());
        assertEquals("teacher", result.getTeacherUsername());
        verify(missionGroupRepository).save(argThat(group ->
                group.getName().equals("Updated Group") &&
                        group.getTeacher().getUsername().equals("teacher")
        ));
    }

    @Test
    @DisplayName("미션 그룹을 업데이트한다 - 실패 (존재X)")
    void updateMissionGroup_notFound() {
        Long nonExistentId = 999L;
        // Given
        MissionGroupRequest request = MissionGroupRequest
                .builder()
                .name("Invalid Group")
                .teacherUsername("teacher")
                .build();
        when(missionGroupRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(MissionGroupNotFoundException.class, () -> missionGroupService.updateMissionGroup(nonExistentId, request));
    }

    @Test
    @DisplayName("미션 그룹을 삭제한다 - 성공")
    void deleteMissionGroup_success() {
        // Given
        Long id = 1L;
        when(missionGroupRepository.existsById(id)).thenReturn(true);

        // When
        missionGroupService.deleteMissionGroup(id);

        // Then
        verify(missionGroupRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("미션 그룹을 삭제한다 - 실패 (존재X)")
    void deleteMissionGroup_notFound() {
        // Given
        Long id = 999L;
        when(missionGroupRepository.existsById(id)).thenReturn(false);

        // When & Then
        assertThrows(MissionGroupNotFoundException.class, () -> missionGroupService.deleteMissionGroup(id));
        verify(missionGroupRepository, never()).deleteById(anyLong());
    }
}