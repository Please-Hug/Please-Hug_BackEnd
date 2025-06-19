package org.example.hugmeexp.domain.missionGroup.service;

import org.example.hugmeexp.domain.missionGroup.dto.request.MissionGroupRequest;
import org.example.hugmeexp.domain.missionGroup.dto.response.MissionGroupResponse;
import org.example.hugmeexp.domain.missionGroup.entity.MissionGroup;
import org.example.hugmeexp.domain.missionGroup.entity.UserMissionGroup;
import org.example.hugmeexp.domain.missionGroup.exception.AlreadyExistsUserMissionGroupException;
import org.example.hugmeexp.domain.missionGroup.exception.MissionGroupNotFoundException;
import org.example.hugmeexp.domain.missionGroup.exception.NotExistsUserMissionGroupException;
import org.example.hugmeexp.domain.missionGroup.exception.UserNotFoundException;
import org.example.hugmeexp.domain.missionGroup.mapper.MissionGroupMapper;
import org.example.hugmeexp.domain.missionGroup.repository.MissionGroupRepository;
import org.example.hugmeexp.domain.missionGroup.repository.UserMissionGroupRepository;
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    @Mock
    private UserMissionGroupRepository userMissionGroupRepository;

    @InjectMocks
    private MissionGroupServiceImpl missionGroupService;

    private final Long SAMPLE_USER_ID = 1L;
    private final Long SAMPLE_GROUP_ID = 10L;


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


    @Test
    @DisplayName("미션 그룹에 유저를 정상적으로 추가한다 - 성공")
    void addUserToMissionGroup_Success() {
        // Given
        MissionGroup missionGroup = MissionGroup.builder().id(SAMPLE_GROUP_ID).build();

        User user = mock(User.class);

        when(userRepository.findById(SAMPLE_USER_ID)).thenReturn(Optional.of(user));
        when(missionGroupRepository.findById(SAMPLE_GROUP_ID)).thenReturn(Optional.of(missionGroup));
        // 아직 유저가 그룹에 속해있지 않음 (false 반환)
        when(userMissionGroupRepository.existsByUserAndMissionGroup(user, missionGroup)).thenReturn(false);

        // When
        // 예외가 발생하지 않아야 함
        assertDoesNotThrow(() -> missionGroupService.addUserToMissionGroup(SAMPLE_USER_ID, SAMPLE_GROUP_ID));

        // Then
        // UserMissionGroupRepository의 save가 한 번 호출되었는지 검증
        verify(userMissionGroupRepository).save(any(UserMissionGroup.class));
    }

    @Test
    @DisplayName("존재하지 않는 유저를 그룹에 추가 시도 - 실패")
    void addUserToMissionGroup_UserNotFound_Fail() {
        // Given
        when(userRepository.findById(SAMPLE_USER_ID)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> missionGroupService.addUserToMissionGroup(SAMPLE_USER_ID, SAMPLE_GROUP_ID))
                .isInstanceOf(UserNotFoundException.class);

        // missionGroupRepository.findById 나 userMissionGroupRepository.save는 호출되면 안 됨
        verify(missionGroupRepository, never()).findById(anyLong());
        verify(userMissionGroupRepository, never()).save(any());
    }

    @Test
    @DisplayName("존재하지 않는 미션 그룹에 유저를 추가 시도 - 실패")
    void addUserToMissionGroup_GroupNotFound_Fail() {
        // Given
        User user = mock(User.class);
        when(userRepository.findById(SAMPLE_USER_ID)).thenReturn(Optional.of(user));
        when(missionGroupRepository.findById(SAMPLE_GROUP_ID)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> missionGroupService.addUserToMissionGroup(SAMPLE_USER_ID, SAMPLE_GROUP_ID))
                .isInstanceOf(MissionGroupNotFoundException.class);

        verify(userMissionGroupRepository, never()).save(any());
    }

    @Test
    @DisplayName("이미 그룹에 속한 유저를 다시 추가 시도 - 실패")
    void addUserToMissionGroup_AlreadyExists_Fail() {
        // Given
        User user = mock(User.class);
        MissionGroup missionGroup = MissionGroup.builder().id(SAMPLE_GROUP_ID).build();

        when(userRepository.findById(SAMPLE_USER_ID)).thenReturn(Optional.of(user));
        when(missionGroupRepository.findById(SAMPLE_GROUP_ID)).thenReturn(Optional.of(missionGroup));
        // 이미 유저가 그룹에 속해있다고 설정 (true 반환)
        when(userMissionGroupRepository.existsByUserAndMissionGroup(user, missionGroup)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> missionGroupService.addUserToMissionGroup(SAMPLE_USER_ID, SAMPLE_GROUP_ID))
                .isInstanceOf(AlreadyExistsUserMissionGroupException.class);

        verify(userMissionGroupRepository, never()).save(any());
    }


    @Test
    @DisplayName("미션 그룹에서 유저를 정상적으로 제거한다 - 성공")
    void removeUserFromMissionGroup_Success() {
        // Given
        User user = mock(User.class);
        MissionGroup missionGroup = MissionGroup.builder().id(SAMPLE_GROUP_ID).build();
        UserMissionGroup userMissionGroup = UserMissionGroup.builder()
                .user(user)
                .missionGroup(missionGroup)
                .build();

        when(userRepository.findById(SAMPLE_USER_ID)).thenReturn(Optional.of(user));
        when(missionGroupRepository.findById(SAMPLE_GROUP_ID)).thenReturn(Optional.of(missionGroup));
        when(userMissionGroupRepository.findByUserAndMissionGroup(user, missionGroup))
                .thenReturn(Optional.of(userMissionGroup));

        // When
        assertDoesNotThrow(() -> missionGroupService.removeUserFromMissionGroup(SAMPLE_USER_ID, SAMPLE_GROUP_ID));

        // Then
        // userMissionGroupRepository의 delete가 정확한 객체로 호출되었는지 검증
        verify(userMissionGroupRepository).delete(userMissionGroup);
    }

    @Test
    @DisplayName("존재하지 않는 유저를 그룹에서 제거 시도 - 실패")
    void removeUserFromMissionGroup_UserNotFound_Fail() {
        // Given
        when(userRepository.findById(SAMPLE_USER_ID)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> missionGroupService.removeUserFromMissionGroup(SAMPLE_USER_ID, SAMPLE_GROUP_ID))
                .isInstanceOf(UserNotFoundException.class);

        verify(userMissionGroupRepository, never()).delete(any());
    }

    @Test
    @DisplayName("존재하지 않는 미션 그룹에서 유저를 제거 시도 - 실패")
    void removeUserFromMissionGroup_GroupNotFound_Fail() {
        // Given
        User user = mock(User.class);
        when(userRepository.findById(SAMPLE_USER_ID)).thenReturn(Optional.of(user));
        when(missionGroupRepository.findById(SAMPLE_GROUP_ID)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> missionGroupService.removeUserFromMissionGroup(SAMPLE_USER_ID, SAMPLE_GROUP_ID))
                .isInstanceOf(MissionGroupNotFoundException.class);

        verify(userMissionGroupRepository, never()).delete(any());
    }

    @Test
    @DisplayName("그룹에 속해있지 않은 유저를 제거 시도 - 실패")
    void removeUserFromMissionGroup_NotAMember_Fail() {
        // Given
        User user = mock(User.class);
        MissionGroup missionGroup = MissionGroup.builder().id(SAMPLE_GROUP_ID).build();

        when(userRepository.findById(SAMPLE_USER_ID)).thenReturn(Optional.of(user));
        when(missionGroupRepository.findById(SAMPLE_GROUP_ID)).thenReturn(Optional.of(missionGroup));
        // 유저-그룹 관계가 존재하지 않음 (Optional.empty() 반환)
        when(userMissionGroupRepository.findByUserAndMissionGroup(user, missionGroup))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> missionGroupService.removeUserFromMissionGroup(SAMPLE_USER_ID, SAMPLE_GROUP_ID))
                .isInstanceOf(NotExistsUserMissionGroupException.class);

        verify(userMissionGroupRepository, never()).delete(any());
    }
}