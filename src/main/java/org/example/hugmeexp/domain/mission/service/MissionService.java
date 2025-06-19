package org.example.hugmeexp.domain.mission.service;

import org.example.hugmeexp.domain.mission.dto.request.MissionRequest;
import org.example.hugmeexp.domain.mission.dto.response.MissionResponse;

import java.util.List;

public interface MissionService {
    MissionResponse createMission(MissionRequest missionRequest);

    MissionResponse getMissionById(Long id);

    List<MissionResponse> getAllMissions();

    MissionResponse updateMission(Long id, MissionRequest missionRequest);

    MissionResponse deleteMission(Long id);

    MissionResponse chaageMissionGroup(Long id, Long missionGroupId);

    List<MissionResponse> getMissionsByMissionGroupId(Long missionGroupId);
}
