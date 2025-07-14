package org.example.hugmeexp.domain.mission.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.hugmeexp.domain.mission.dto.request.MissionRequest;
import org.example.hugmeexp.domain.mission.dto.response.MissionResponse;
import org.example.hugmeexp.domain.mission.entity.*;
import org.example.hugmeexp.domain.mission.exception.*;
import org.example.hugmeexp.domain.mission.mapper.MissionMapper;
import org.example.hugmeexp.domain.mission.repository.*;
import org.example.hugmeexp.domain.missionGroup.entity.MissionGroup;
import org.example.hugmeexp.domain.missionGroup.exception.MissionGroupNotFoundException;
import org.example.hugmeexp.domain.missionGroup.repository.MissionGroupRepository;
import org.example.hugmeexp.global.common.service.CacheService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MissionServiceImpl implements MissionService {
    private final MissionRepository missionRepository;
    private final MissionGroupRepository missionGroupRepository;
    private final MissionMapper missionMapper;

    private final CacheService cacheService;
    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "missionsByMissionGroupId", key = "#missionRequest.getMissionGroupId()"),
            }
    )
    public MissionResponse createMission(MissionRequest missionRequest) {
        Mission mission = missionMapper.toEntity(missionRequest);

        MissionGroup missionGroup = missionGroupRepository.findById(missionRequest.getMissionGroupId())
                .orElseThrow(MissionGroupNotFoundException::new);

        mission = mission.toBuilder()
                .missionGroup(missionGroup)
                .build();

        Mission savedMission = missionRepository.save(mission);
        return missionMapper.toMissionResponse(savedMission);
    }

    @Override
    public MissionResponse getMissionById(Long id) {
        Mission mission = missionRepository.findById(id)
                .orElseThrow(MissionNotFoundException::new);
        return missionMapper.toMissionResponse(mission);
    }

    @Override
    public List<MissionResponse> getAllMissions() {
        List<Mission> missions = missionRepository.findAll();
        return missions.stream()
                .map(missionMapper::toMissionResponse)
                .toList();
    }

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "missionsByMissionGroupId", key = "#missionRequest.getMissionGroupId()"),
            }
    )
    public MissionResponse updateMission(Long id, MissionRequest missionRequest) {
        Mission mission = missionRepository.findById(id)
                .orElseThrow(MissionNotFoundException::new);

        mission = mission.toBuilder()
                .name(missionRequest.getName())
                .description(missionRequest.getDescription())
                .difficulty(missionRequest.getDifficulty())
                .rewardPoint(missionRequest.getRewardPoint())
                .rewardExp(missionRequest.getRewardExp())
                .order(missionRequest.getOrder())
                .line(missionRequest.getLine())
                .tip(missionRequest.getTip())
                .build();

        Mission updatedMission = missionRepository.save(mission);
        return missionMapper.toMissionResponse(updatedMission);
    }

    @Override
    @Transactional
    public void deleteMission(Long id) {
        Mission mission = missionRepository.findById(id)
                .orElseThrow(MissionNotFoundException::new);
        cacheService.evictMissionByMissionGroupCache(mission.getMissionGroup().getId());
        missionRepository.delete(mission);
    }

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "missionsByMissionGroupId", key = "#missionGroupId")
            }
    )
    public MissionResponse changeMissionGroup(Long id, Long missionGroupId) {
        Mission mission = missionRepository.findById(id)
                .orElseThrow(MissionNotFoundException::new);

        MissionGroup missionGroup = missionGroupRepository.findById(missionGroupId)
                .orElseThrow(MissionGroupNotFoundException::new);

        mission = mission.toBuilder()
                .missionGroup(missionGroup)
                .build();

        Mission updatedMission = missionRepository.save(mission);
        return missionMapper.toMissionResponse(updatedMission);
    }

    @Override
    @Cacheable(value = "missionsByMissionGroupId", key = "#missionGroupId")
    public List<MissionResponse> getMissionsByMissionGroupId(Long missionGroupId) {
        MissionGroup missionGroup = missionGroupRepository.findById(missionGroupId)
                .orElseThrow(MissionGroupNotFoundException::new);

        List<Mission> missions = missionRepository.findMissionByMissionGroup(missionGroup);

        return missions.stream()
                .map(missionMapper::toMissionResponse)
                .collect(Collectors.toList());
    }
}
