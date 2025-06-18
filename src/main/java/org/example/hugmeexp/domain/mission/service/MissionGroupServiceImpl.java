package org.example.hugmeexp.domain.mission.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.hugmeexp.domain.mission.dto.request.MissionGroupRequest;
import org.example.hugmeexp.domain.mission.dto.response.MissionGroupResponse;
import org.example.hugmeexp.domain.mission.dto.request.MissionGroupRequest;
import org.example.hugmeexp.domain.mission.entity.MissionGroup;
import org.example.hugmeexp.domain.mission.exception.MissionGroupNotFoundException;
import org.example.hugmeexp.domain.mission.mapper.MissionGroupMapper;
import org.example.hugmeexp.domain.mission.repository.MissionGroupRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MissionGroupServiceImpl implements MissionGroupService {
    private final MissionGroupRepository missionGroupRepository;
    private final MissionGroupMapper missionGroupMapper;
    @Override
    public List<MissionGroupResponse> getAllMissionGroups() {
        return missionGroupRepository.findAll()
                .stream()
                .map(missionGroupMapper::toMissionGroupResponse)
                .toList();
    }

    @Override
    @Transactional
    public MissionGroupResponse createMissionGroup(MissionGroupRequest request) {
        MissionGroup missionGroup = missionGroupMapper.toEntity(request);
        var savedMissionGroup = missionGroupRepository.save(missionGroup);
        return missionGroupMapper.toMissionGroupResponse(savedMissionGroup);
    }

    @Override
    public MissionGroupResponse getMissionById(Long id) {
        return missionGroupRepository.findById(id)
                .map(missionGroupMapper::toMissionGroupResponse)
                .orElseThrow(MissionGroupNotFoundException::new);
    }

    @Override
    @Transactional
    public MissionGroupResponse updateMissionGroup(Long id, MissionGroupRequest request) {
        MissionGroup missionGroup = missionGroupRepository.findById(id)
                .orElseThrow(MissionGroupNotFoundException::new);

        missionGroup = missionGroup.toBuilder()
                .teacherId(request.getTeacherId())
                .name(request.getName())
                .build();
        var updatedMissionGroup = missionGroupRepository.save(missionGroup);

        return missionGroupMapper.toMissionGroupResponse(updatedMissionGroup);
    }

    @Override
    @Transactional
    public void deleteMissionGroup(Long id) {
        if (!missionGroupRepository.existsById(id)) {
            throw new MissionGroupNotFoundException();
        }
        missionGroupRepository.deleteById(id);
    }
}
