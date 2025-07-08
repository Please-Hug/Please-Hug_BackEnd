package org.example.hugmeexp.domain.mission.service;

import org.example.hugmeexp.domain.mission.dto.request.MissionRequest;
import org.example.hugmeexp.domain.mission.dto.response.MissionResponse;
import org.example.hugmeexp.domain.mission.entity.*;
import org.example.hugmeexp.domain.mission.enums.Difficulty;
import org.example.hugmeexp.domain.mission.exception.*;
import org.example.hugmeexp.domain.mission.mapper.MissionMapper;
import org.example.hugmeexp.domain.mission.repository.*;
import org.example.hugmeexp.domain.missionGroup.entity.MissionGroup;
import org.example.hugmeexp.domain.missionGroup.exception.MissionGroupNotFoundException;
import org.example.hugmeexp.domain.missionGroup.repository.MissionGroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("미션 서비스 테스트")
class MissionServiceTest {

    @Mock
    private MissionRepository missionRepository;

    @Mock
    private MissionGroupRepository missionGroupRepository;

    @Mock
    private MissionMapper missionMapper;

    @InjectMocks
    private MissionServiceImpl missionService;


    private final Long SAMPLE_ID = 1L;
    private final String SAMPLE_NAME = "샘플 미션";

    private final MissionRequest SAMPLE_REQUEST = MissionRequest.builder()
            .name("미션명")
            .description("미션 설명")
            .difficulty(Difficulty.HARD)
            .rewardPoint(100)
            .rewardExp(50)
            .order(1)
            .missionGroupId(SAMPLE_ID)
            .tip("미션 팁")
            .build();


    @Test
    @DisplayName("미션을 정상적으로 생성한다 - 성공")
    void createMission_Success() {
        // Given
        MissionGroup group = MissionGroup.builder().id(SAMPLE_ID).build();
        Mission mission = Mission.builder().id(SAMPLE_ID).build();
        MissionResponse expectedResponse = MissionResponse.builder().id(SAMPLE_ID).name(SAMPLE_NAME).build();

        when(missionGroupRepository.findById(SAMPLE_ID)).thenReturn(Optional.of(group));
        when(missionMapper.toEntity(SAMPLE_REQUEST)).thenReturn(mission);
        when(missionRepository.save(any(Mission.class))).thenReturn(mission);
        when(missionMapper.toMissionResponse(mission)).thenReturn(expectedResponse);

        // When
        MissionResponse result = missionService.createMission(SAMPLE_REQUEST);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        verify(missionRepository).save(any(Mission.class));
    }

    @Test
    @DisplayName("존재하지 않는 미션 그룹으로 생성 시도 - 실패")
    void createMission_NonExistingGroup_Fail() {
        // Given
        when(missionGroupRepository.findById(SAMPLE_ID)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> missionService.createMission(SAMPLE_REQUEST))
                .isInstanceOf(MissionGroupNotFoundException.class);
        verify(missionRepository, never()).save(any());
    }

    @Test
    @DisplayName("ID로 미션을 정상적으로 조회한다 - 성공")
    void getMissionById_Success() {
        // Given
        Mission mission = Mission.builder().id(SAMPLE_ID).build();
        MissionResponse expectedResponse = MissionResponse.builder().id(SAMPLE_ID).name(SAMPLE_NAME).build();

        when(missionRepository.findById(SAMPLE_ID)).thenReturn(Optional.of(mission));
        when(missionMapper.toMissionResponse(mission)).thenReturn(expectedResponse);

        // When
        MissionResponse result = missionService.getMissionById(SAMPLE_ID);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("존재하지 않는 ID로 미션 조회 시도 - 실패")
    void getMissionById_NonExistingId_Fail() {
        // Given
        when(missionRepository.findById(SAMPLE_ID)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> missionService.getMissionById(SAMPLE_ID))
                .isInstanceOf(MissionNotFoundException.class);
    }

    @Test
    @DisplayName("모든 미션을 정상적으로 조회한다 - 성공")
    void getAllMissions_Success() {
        // Given
        Mission sampleMission = Mission.builder().id(SAMPLE_ID).build();
        List<Mission> missionList = List.of(sampleMission);
        MissionResponse sampleResponse = MissionResponse.builder().id(SAMPLE_ID).name(SAMPLE_NAME).build();

        when(missionRepository.findAll()).thenReturn(missionList);
        when(missionMapper.toMissionResponse(sampleMission)).thenReturn(sampleResponse);

        // When
        List<MissionResponse> result = missionService.getAllMissions();

        // Then
        assertThat(result)
                .hasSize(1)
                .containsExactly(sampleResponse);
    }

    @Test
    @DisplayName("빈 미션 목록을 정상적으로 조회한다 - 성공")
    void getAllMissions_EmptyList_Success() {
        // Given
        when(missionRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<MissionResponse> result = missionService.getAllMissions();

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("미션 정보를 정상적으로 수정한다 - 성공")
    void updateMission_Success() {
        // Given
        Mission existingMission = Mission.builder()
                .id(SAMPLE_ID)
                .name("기존 이름")
                .build();

        Mission updatedMission = Mission.builder()
                .id(SAMPLE_ID)
                .name(SAMPLE_REQUEST.getName())
                .description(SAMPLE_REQUEST.getDescription())
                .difficulty(SAMPLE_REQUEST.getDifficulty())
                .rewardPoint(SAMPLE_REQUEST.getRewardPoint())
                .rewardExp(SAMPLE_REQUEST.getRewardExp())
                .order(SAMPLE_REQUEST.getOrder())
                .tip(SAMPLE_REQUEST.getTip())
                .build();

        MissionResponse expectedResponse = MissionResponse.builder().id(SAMPLE_ID).name("미션명").build();

        when(missionRepository.findById(SAMPLE_ID)).thenReturn(Optional.of(existingMission));
        when(missionRepository.save(any(Mission.class))).thenReturn(updatedMission);
        when(missionMapper.toMissionResponse(updatedMission)).thenReturn(expectedResponse);

        // When
        MissionResponse result = missionService.updateMission(SAMPLE_ID, SAMPLE_REQUEST);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        verify(missionRepository).save(argThat(m -> {
            assertThat(m.getName()).isEqualTo("미션명");
            assertThat(m.getDescription()).isEqualTo("미션 설명");
            assertThat(m.getTip()).isEqualTo(SAMPLE_REQUEST.getTip());
            return true;
        }));
    }

    @Test
    @DisplayName("존재하지 않는 미션 수정 시도 - 실패")
    void updateMission_NonExistingMission_Fail() {
        // Given
        when(missionRepository.findById(SAMPLE_ID)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> missionService.updateMission(SAMPLE_ID, SAMPLE_REQUEST))
                .isInstanceOf(MissionNotFoundException.class);
        verify(missionRepository, never()).save(any());
    }

    @Test
    @DisplayName("미션을 정상적으로 삭제한다 - 성공")
    void deleteMission_Success() {
        // Given
        Mission mission = Mission.builder().id(SAMPLE_ID).build();
        when(missionRepository.findById(SAMPLE_ID)).thenReturn(Optional.of(mission));

        // When
        missionService.deleteMission(SAMPLE_ID);

        // Then
        verify(missionRepository).delete(mission);
    }

    @Test
    @DisplayName("존재하지 않는 미션 삭제 시도 - 실패")
    void deleteMission_NonExistingMission_Fail() {
        // Given
        when(missionRepository.findById(SAMPLE_ID)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> missionService.deleteMission(SAMPLE_ID))
                .isInstanceOf(MissionNotFoundException.class);
        verify(missionRepository, never()).delete(any());
    }

    @Test
    @DisplayName("미션 그룹을 정상적으로 변경한다 - 성공")
    void changeMissionGroup_Success() {
        // Given
        MissionGroup oldGroup = MissionGroup.builder().id(1L).build();
        MissionGroup newGroup = MissionGroup.builder().id(2L).build();

        Mission existingMission = Mission.builder()
                .id(SAMPLE_ID)
                .missionGroup(oldGroup)
                .build();

        Mission updatedMission = Mission.builder()
                .id(SAMPLE_ID)
                .missionGroup(newGroup)
                .build();

        MissionResponse expectedResponse = MissionResponse.builder()
                .id(SAMPLE_ID)
                .name("변경된 미션")
                .build();

        when(missionRepository.findById(SAMPLE_ID)).thenReturn(Optional.of(existingMission));
        when(missionGroupRepository.findById(2L)).thenReturn(Optional.of(newGroup));
        when(missionRepository.save(any(Mission.class))).thenReturn(updatedMission);
        when(missionMapper.toMissionResponse(updatedMission)).thenReturn(expectedResponse);

        // When
        MissionResponse result = missionService.changeMissionGroup(SAMPLE_ID, 2L);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        verify(missionRepository).save(argThat(m -> {
            assertThat(m.getMissionGroup().getId()).isEqualTo(2L);
            return true;
        }));
    }

    @Test
    @DisplayName("존재하지 않는 그룹으로 변경 시도 - 실패")
    void changeMissionGroup_NonExistingGroup_Fail() {
        // Given
        Mission mission = Mission.builder().id(SAMPLE_ID).build();
        when(missionRepository.findById(SAMPLE_ID)).thenReturn(Optional.of(mission));
        when(missionGroupRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> missionService.changeMissionGroup(SAMPLE_ID, 999L))
                .isInstanceOf(MissionGroupNotFoundException.class);
        verify(missionRepository, never()).save(any());
    }

    @Test
    @DisplayName("특정 그룹의 미션들을 정상적으로 조회한다 - 성공")
    void getMissionsByMissionGroupId_Success() {
        // Given
        MissionGroup group = MissionGroup.builder().id(SAMPLE_ID).build();
        Mission mission = Mission.builder().id(SAMPLE_ID).build();
        MissionResponse response = MissionResponse.builder()
                .id(SAMPLE_ID)
                .name(SAMPLE_NAME)
                .build();

        when(missionGroupRepository.findById(SAMPLE_ID)).thenReturn(Optional.of(group));
        when(missionRepository.findMissionByMissionGroup(group)).thenReturn(List.of(mission));
        when(missionMapper.toMissionResponse(mission)).thenReturn(response);

        // When
        List<MissionResponse> result = missionService.getMissionsByMissionGroupId(SAMPLE_ID);

        // Then
        assertThat(result)
                .hasSize(1)
                .containsExactly(response);
    }

    @Test
    @DisplayName("미션이 없는 그룹 조회 시 빈 목록 반환 - 성공")
    void getMissionsByMissionGroupId_EmptyGroup_Success() {
        // Given
        MissionGroup group = MissionGroup.builder().id(SAMPLE_ID).build();
        when(missionGroupRepository.findById(SAMPLE_ID)).thenReturn(Optional.of(group));
        when(missionRepository.findMissionByMissionGroup(group)).thenReturn(Collections.emptyList());

        // When
        List<MissionResponse> result = missionService.getMissionsByMissionGroupId(SAMPLE_ID);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 그룹의 미션 조회 시도 - 실패")
    void getMissionsByMissionGroupId_NonExistingGroup_Fail() {
        // Given
        when(missionGroupRepository.findById(SAMPLE_ID)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> missionService.getMissionsByMissionGroupId(SAMPLE_ID))
                .isInstanceOf(MissionGroupNotFoundException.class);
        verify(missionRepository, never()).findMissionByMissionGroup(any());
    }
}