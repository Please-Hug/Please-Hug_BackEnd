package org.example.hugmeexp.domain.qeust.validator;

import lombok.RequiredArgsConstructor;
import org.example.hugmeexp.domain.mission.entity.UserMissionStateLog;
import org.example.hugmeexp.domain.mission.enums.UserMissionState;
import org.example.hugmeexp.domain.mission.repository.UserMissionRepository;
import org.example.hugmeexp.domain.mission.repository.UserMissionStateLogRepository;
import org.example.hugmeexp.domain.qeust.entity.UserQuest;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Component
public class MissionRewardValidator implements QuestValidator {

    private final UserMissionStateLogRepository userMissionStateLogRepository;

    @Override
    public boolean isValid(UserQuest userQuest) {

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        List<UserMissionStateLog> logs = userMissionStateLogRepository.findByUserIdAndCreatedAtBetween(userQuest.getUser().getId(), startOfDay, endOfDay);

        return logs.stream()
                .anyMatch(log -> log.getNextState() == UserMissionState.REWARD_RECEIVED);
    }
}