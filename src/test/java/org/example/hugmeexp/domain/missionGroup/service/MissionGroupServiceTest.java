package org.example.hugmeexp.domain.missionGroup.service;

import org.example.hugmeexp.domain.mission.dto.response.UserMissionResponse;
import org.example.hugmeexp.domain.mission.entity.UserMission;
import org.example.hugmeexp.domain.mission.mapper.UserMissionMapper;
import org.example.hugmeexp.domain.mission.repository.UserMissionRepository;
import org.example.hugmeexp.domain.missionGroup.dto.request.MissionGroupRequest;
import org.example.hugmeexp.domain.missionGroup.dto.response.MissionGroupResponse;
import org.example.hugmeexp.domain.missionGroup.dto.response.UserMissionGroupResponse;
import org.example.hugmeexp.domain.missionGroup.entity.MissionGroup;
import org.example.hugmeexp.domain.missionGroup.entity.UserMissionGroup;
import org.example.hugmeexp.domain.missionGroup.exception.*;
import org.example.hugmeexp.domain.missionGroup.mapper.MissionGroupMapper;
import org.example.hugmeexp.domain.missionGroup.mapper.UserMissionGroupMapper;
import org.example.hugmeexp.domain.missionGroup.repository.MissionGroupRepository;
import org.example.hugmeexp.domain.missionGroup.repository.UserMissionGroupRepository;
import org.example.hugmeexp.domain.user.dto.response.UserProfileResponse;
import org.example.hugmeexp.domain.user.repository.UserRepository;
import org.example.hugmeexp.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
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

    @Mock
    private UserMissionMapper userMissionMapper;

    @Mock
    private UserMissionRepository userMissionRepository;

    @Mock
    private UserMissionGroupMapper userMissionGroupMapper;

    @Mock
    private CacheManager cacheManager;

    @InjectMocks
    private MissionGroupServiceImpl missionGroupService;

    private final Long SAMPLE_USER_ID = 1L;
    private final Long SAMPLE_GROUP_ID = 10L;

    private final String SAMPLE_USERNAME = "testUser";


    @Test
    @DisplayName("모든 미션 그룹을 조회한다 - 성공")
    void getAllMissionGroups() {
        UserProfileResponse teacher1 = new UserProfileResponse(
                "", "teacher1", "Teacher One");
        UserProfileResponse teacher2 = new UserProfileResponse(
                "", "teacher2", "Teacher Two");
        // Given
        MissionGroup group1 = mock(MissionGroup.class);
        MissionGroup group2 = mock(MissionGroup.class);
        MissionGroupResponse response1 = MissionGroupResponse
                .builder()
                .id(1L)
                .name("Group1")
                .teacher(teacher1)
                .build();
        MissionGroupResponse response2 = MissionGroupResponse
                .builder()
                .id(2L)
                .name("Group2")
                .teacher(teacher2)
                .build();
        when(missionGroupRepository.findAllWithTeacher()).thenReturn(List.of(group1, group2));
        when(missionGroupMapper.toMissionGroupResponse(group1)).thenReturn(response1);
        when(missionGroupMapper.toMissionGroupResponse(group2)).thenReturn(response2);

        // When
        List<MissionGroupResponse> result = missionGroupService.getAllMissionGroups();

        // Then
        assertEquals(2, result.size());
        assertEquals("Group1", result.get(0).getName());
        assertEquals("Group2", result.get(1).getName());
        verify(missionGroupRepository, times(1)).findAllWithTeacher();
    }

    @Test
    @DisplayName("새로운 미션 그룹을 생성한다 - 성공")
    void createMissionGroup() {
        // Given
        UserProfileResponse teacher = new UserProfileResponse(
                "", "teacher1", "teacher1");

        User user1 = User.createUser("teacher1", "password", "Teacher One", "1234");
        User user2 = User.createUser("admin", "password", "Admin User", "1234");

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
                .teacher(teacher)
                .build();

        when(missionGroupRepository.save(any(MissionGroup.class))).thenReturn(savedGroup);
        when(missionGroupMapper.toMissionGroupResponse(savedGroup)).thenReturn(expectedResponse);
        when(userRepository.findByUsername("teacher1")).thenReturn(Optional.of(user1));
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user2));

        // When
        MissionGroupResponse result = missionGroupService.createMissionGroup(request, "admin");

        // Then
        assertEquals("New Group", result.getName());
        assertEquals("teacher1", result.getTeacher().getUsername());
        verify(missionGroupRepository, times(1)).save(any(MissionGroup.class));
    }

    @Test
    @DisplayName("ID로 미션 그룹을 조회한다 - 존재O")
    void getMissionGroupById_found() {
        // Given
        UserProfileResponse teacher = new UserProfileResponse(
                "", "teacher1", "Teacher One");

        Long id = 1L;
        MissionGroup group = mock(MissionGroup.class);
        MissionGroupResponse expectedResponse = MissionGroupResponse
                .builder()
                .id(id)
                .name("Existing Group")
                .teacher(teacher)
                .build();

        when(missionGroupRepository.findByIdWithTeacher(id)).thenReturn(Optional.of(group));
        when(missionGroupMapper.toMissionGroupResponse(group)).thenReturn(expectedResponse);

        // When
        MissionGroupResponse result = missionGroupService.getMissionGroupById(id).get(0);

        // Then
        assertNotNull(result);
        assertEquals("Existing Group", result.getName());
    }

    @Test
    @DisplayName("ID로 미션 그룹을 조회한다 - 존재X")
    void getMissionGroupById_notFound() {
        // Given
        Long id = 999L;
        when(missionGroupRepository.findByIdWithTeacher(id)).thenReturn(Optional.empty());

        // When & Then
        MissionGroupNotFoundException exception = assertThrows(MissionGroupNotFoundException.class,
                () -> missionGroupService.getMissionGroupById(id));

        assertNotNull(exception);
    }

    @Test
    @DisplayName("미션 그룹을 업데이트한다 - 성공")
    void updateMissionGroup_success() {
        UserProfileResponse teacherResponse = new UserProfileResponse(
                "", "teacher", "teacher");

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
                .teacher(teacherResponse)
                .build();

        when(missionGroupRepository.findById(id)).thenReturn(Optional.of(existingGroup));
        when(missionGroupRepository.save(any())).thenReturn(existingGroup);
        when(missionGroupMapper.toMissionGroupResponse(any())).thenReturn(expectedResponse);
        when(userRepository.findByUsername("teacher")).thenReturn(Optional.of(teacher));

        // When
        MissionGroupResponse result = missionGroupService.updateMissionGroup(id, request);

        // Then
        assertEquals("Updated Group", result.getName());
        assertEquals("teacher", result.getTeacher().getUsername());
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

        when(userRepository.findByUsername(SAMPLE_USERNAME)).thenReturn(Optional.of(user));
        when(missionGroupRepository.findById(SAMPLE_GROUP_ID)).thenReturn(Optional.of(missionGroup));
        // 아직 유저가 그룹에 속해있지 않음 (false 반환)
        when(userMissionGroupRepository.existsByUserAndMissionGroup(user, missionGroup)).thenReturn(false);

        // When
        // 예외가 발생하지 않아야 함
        assertDoesNotThrow(() -> missionGroupService.addUserToMissionGroup(SAMPLE_USERNAME, SAMPLE_GROUP_ID));

        // Then
        // UserMissionGroupRepository의 save가 한 번 호출되었는지 검증
        verify(userMissionGroupRepository).save(any(UserMissionGroup.class));
    }

    @Test
    @DisplayName("존재하지 않는 유저를 그룹에 추가 시도 - 실패")
    void addUserToMissionGroup_UserNotFound_Fail() {
        // Given
        when(userRepository.findByUsername(SAMPLE_USERNAME)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> missionGroupService.addUserToMissionGroup(SAMPLE_USERNAME, SAMPLE_GROUP_ID))
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
        when(userRepository.findByUsername(SAMPLE_USERNAME)).thenReturn(Optional.of(user));
        when(missionGroupRepository.findById(SAMPLE_GROUP_ID)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> missionGroupService.addUserToMissionGroup(SAMPLE_USERNAME, SAMPLE_GROUP_ID))
                .isInstanceOf(MissionGroupNotFoundException.class);

        verify(userMissionGroupRepository, never()).save(any());
    }

    @Test
    @DisplayName("이미 그룹에 속한 유저를 다시 추가 시도 - 실패")
    void addUserToMissionGroup_AlreadyExists_Fail() {
        // Given
        User user = mock(User.class);
        MissionGroup missionGroup = MissionGroup.builder().id(SAMPLE_GROUP_ID).build();

        when(userRepository.findByUsername(SAMPLE_USERNAME)).thenReturn(Optional.of(user));
        when(missionGroupRepository.findById(SAMPLE_GROUP_ID)).thenReturn(Optional.of(missionGroup));
        // 이미 유저가 그룹에 속해있다고 설정 (true 반환)
        when(userMissionGroupRepository.existsByUserAndMissionGroup(user, missionGroup)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> missionGroupService.addUserToMissionGroup(SAMPLE_USERNAME, SAMPLE_GROUP_ID))
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

        when(userRepository.findByUsername(SAMPLE_USERNAME)).thenReturn(Optional.of(user));
        when(missionGroupRepository.findById(SAMPLE_GROUP_ID)).thenReturn(Optional.of(missionGroup));
        when(userMissionGroupRepository.findByUserAndMissionGroup(user, missionGroup))
                .thenReturn(Optional.of(userMissionGroup));

        // When
        assertDoesNotThrow(() -> missionGroupService.removeUserFromMissionGroup(SAMPLE_USERNAME, SAMPLE_GROUP_ID));

        // Then
        // userMissionGroupRepository의 delete가 정확한 객체로 호출되었는지 검증
        verify(userMissionGroupRepository).delete(userMissionGroup);
    }

    @Test
    @DisplayName("존재하지 않는 유저를 그룹에서 제거 시도 - 실패")
    void removeUserFromMissionGroup_UserNotFound_Fail() {
        // Given
        when(userRepository.findByUsername(SAMPLE_USERNAME)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> missionGroupService.removeUserFromMissionGroup(SAMPLE_USERNAME, SAMPLE_GROUP_ID))
                .isInstanceOf(UserNotFoundException.class);

        verify(userMissionGroupRepository, never()).delete(any());
    }

    @Test
    @DisplayName("존재하지 않는 미션 그룹에서 유저를 제거 시도 - 실패")
    void removeUserFromMissionGroup_GroupNotFound_Fail() {
        // Given
        User user = mock(User.class);
        when(userRepository.findByUsername(SAMPLE_USERNAME)).thenReturn(Optional.of(user));
        when(missionGroupRepository.findById(SAMPLE_GROUP_ID)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> missionGroupService.removeUserFromMissionGroup(SAMPLE_USERNAME, SAMPLE_GROUP_ID))
                .isInstanceOf(MissionGroupNotFoundException.class);

        verify(userMissionGroupRepository, never()).delete(any());
    }

    @Test
    @DisplayName("그룹에 속해있지 않은 유저를 제거 시도 - 실패")
    void removeUserFromMissionGroup_NotAMember_Fail() {
        // Given
        User user = mock(User.class);
        MissionGroup missionGroup = MissionGroup.builder().id(SAMPLE_GROUP_ID).build();

        when(userRepository.findByUsername(SAMPLE_USERNAME)).thenReturn(Optional.of(user));
        when(missionGroupRepository.findById(SAMPLE_GROUP_ID)).thenReturn(Optional.of(missionGroup));
        // 유저-그룹 관계가 존재하지 않음 (Optional.empty() 반환)
        when(userMissionGroupRepository.findByUserAndMissionGroup(user, missionGroup))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> missionGroupService.removeUserFromMissionGroup(SAMPLE_USERNAME, SAMPLE_GROUP_ID))
                .isInstanceOf(NotExistsUserMissionGroupException.class);

        verify(userMissionGroupRepository, never()).delete(any());
    }

    @Test
    @DisplayName("유저 이름과 그룹 ID로 유저 미션 목록 조회 - 성공")
    void findUserMissionByUsernameAndMissionGroup_Success() {
        // given
        User user = mock(User.class);
        MissionGroup mockGroup = MissionGroup.builder().id(SAMPLE_GROUP_ID).name("그룹A").build();
        UserMissionGroup mockUMG =
                UserMissionGroup.builder().id(100L).user(user).missionGroup(mockGroup).build();

        // 유저 미션 엔티티와 응답 DTO 준비
        var entity1 = mock(UserMission.class);
        var entity2 = mock(UserMission.class);
        var resp1 = mock(UserMissionResponse.class);
        var resp2 = mock(UserMissionResponse.class);

        given(userRepository.findByUsername(SAMPLE_USERNAME))
                .willReturn(Optional.of(user));
        given(missionGroupRepository.findById(SAMPLE_GROUP_ID))
                .willReturn(Optional.of(mockGroup));
        given(userMissionGroupRepository.findByUserAndMissionGroup(user, mockGroup))
                .willReturn(Optional.of(mockUMG));
        given(userMissionRepository.findByUserAndUserMissionGroup(user, mockUMG))
                .willReturn(List.of(entity1, entity2));
        given(userMissionMapper.toUserMissionResponse(entity1)).willReturn(resp1);
        given(userMissionMapper.toUserMissionResponse(entity2)).willReturn(resp2);

        // when
        List<UserMissionResponse> result =
                missionGroupService.findUserMissionByUsernameAndMissionGroup(
                        SAMPLE_USERNAME, SAMPLE_GROUP_ID);

        // then
        assertThat(result).hasSize(2)
                .containsExactly(resp1, resp2);
    }

    @Test
    @DisplayName("유저가 존재하지 않을 때 예외 발생")
    void findUserMissionByUsernameAndMissionGroup_UserNotFound() {
        given(userRepository.findByUsername(SAMPLE_USERNAME))
                .willReturn(Optional.empty());

        assertThatThrownBy(() ->
                missionGroupService.findUserMissionByUsernameAndMissionGroup(
                        SAMPLE_USERNAME, SAMPLE_GROUP_ID))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("미션 그룹이 존재하지 않을 때 예외 발생")
    void findUserMissionByUsernameAndMissionGroup_GroupNotFound() {
        User mockUser = mock(User.class);
        given(userRepository.findByUsername(SAMPLE_USERNAME))
                .willReturn(Optional.of(mockUser));
        given(missionGroupRepository.findById(SAMPLE_GROUP_ID))
                .willReturn(Optional.empty());

        assertThatThrownBy(() ->
                missionGroupService.findUserMissionByUsernameAndMissionGroup(
                        SAMPLE_USERNAME, SAMPLE_GROUP_ID))
                .isInstanceOf(MissionGroupNotFoundException.class);
    }

    @Test
    @DisplayName("유저-미션 그룹 관계가 존재하지 않을 때 예외 발생")
    void findUserMissionByUsernameAndMissionGroup_UserMissionGroupNotFound() {
        User mockUser = mock(User.class);
        MissionGroup mockGroup = MissionGroup.builder().id(SAMPLE_GROUP_ID).build();

        given(userRepository.findByUsername(SAMPLE_USERNAME))
                .willReturn(Optional.of(mockUser));
        given(missionGroupRepository.findById(SAMPLE_GROUP_ID))
                .willReturn(Optional.of(mockGroup));
        given(userMissionGroupRepository.findByUserAndMissionGroup(mockUser, mockGroup))
                .willReturn(Optional.empty());

        assertThatThrownBy(() ->
                missionGroupService.findUserMissionByUsernameAndMissionGroup(
                        SAMPLE_USERNAME, SAMPLE_GROUP_ID))
                .isInstanceOf(UserMissionGroupNotFoundException.class);
    }

    @Test
    @DisplayName("내 미션 그룹 조회 - 성공")
    void getMyMissionGroups_Success() {
        // Given
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(userRepository.findByUsername(SAMPLE_USERNAME)).thenReturn(Optional.of(user));
        when(userMissionGroupRepository.findByUserIdWithTeacher(anyLong())).thenReturn(List.of(mock(UserMissionGroup.class)));
        when(userMissionGroupMapper.toUserMissionGroupResponse(any())).thenReturn(mock(UserMissionGroupResponse.class));

        // when
        List<UserMissionGroupResponse> result = missionGroupService.getMyMissionGroups(SAMPLE_USERNAME);

        // then
        assertThat(result).isNotEmpty();
        verify(userMissionGroupRepository, times(1)).findByUserIdWithTeacher(anyLong());
    }

    @Test
    @DisplayName("내 미션 그룹 조회 유저가 존재하지 않을 때 - 실패")
    void getMyMissionGroups_UserNotFound() {
        // Given
        when(userRepository.findByUsername(SAMPLE_USERNAME)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> missionGroupService.getMyMissionGroups(SAMPLE_USERNAME))
                .isInstanceOf(UserNotFoundException.class);

        verify(userMissionGroupRepository, never()).findByUserId(anyLong());
    }

    @Test
    @DisplayName("미션 그룹 내 유저 조회 - 성공")
    void getUsersInMissionGroup_Success() {
        // Given
        Long groupId = SAMPLE_GROUP_ID;
        MissionGroup mockGroup = mock(MissionGroup.class);
        User user1 = mock(User.class);
        User user2 = mock(User.class);
        when(user1.getPublicProfileImageUrl()).thenReturn("img1");
        when(user1.getUsername()).thenReturn("user1");
        when(user1.getName()).thenReturn("User One");
        when(user2.getPublicProfileImageUrl()).thenReturn("img2");
        when(user2.getUsername()).thenReturn("user2");
        when(user2.getName()).thenReturn("User Two");
        UserMissionGroup umg1 = mock(UserMissionGroup.class);
        UserMissionGroup umg2 = mock(UserMissionGroup.class);
        when(missionGroupRepository.findById(groupId)).thenReturn(Optional.of(mockGroup));
        when(userMissionGroupRepository.findUsersByMissionGroup(mockGroup)).thenReturn(List.of(user1, user2));

        // When
        List<UserProfileResponse> result = missionGroupService.getUsersInMissionGroup(groupId);

        // Then
        assertEquals(2, result.size());
        assertEquals("user1", result.get(0).getUsername());
        assertEquals("user2", result.get(1).getUsername());
        assertEquals("User One", result.get(0).getName());
        assertEquals("User Two", result.get(1).getName());
        assertEquals("img1", result.get(0).getProfileImage());
        assertEquals("img2", result.get(1).getProfileImage());
        verify(missionGroupRepository, times(1)).findById(groupId);
        verify(userMissionGroupRepository, times(1)).findUsersByMissionGroup(mockGroup);
    }

    @Test
    @DisplayName("미션 그룹 내 유저 조회 그룹 없음 - 실패")
    void getUsersInMissionGroup_GroupNotFound() {
        // Given
        Long groupId = SAMPLE_GROUP_ID;
        when(missionGroupRepository.findById(groupId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(MissionGroupNotFoundException.class, () -> missionGroupService.getUsersInMissionGroup(groupId));
        verify(missionGroupRepository, times(1)).findById(groupId);
        verify(userMissionGroupRepository, never()).findAllByMissionGroup(any());
    }


}