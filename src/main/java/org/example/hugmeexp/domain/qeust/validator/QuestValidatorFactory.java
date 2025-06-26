package org.example.hugmeexp.domain.qeust.validator;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.example.hugmeexp.domain.qeust.enums.QuestType;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class QuestValidatorFactory {

    private final AttendanceValidator attendanceValidator;
    private final QuestClearValidator questClearValidator;
    private final MissionRewardValidator missionRewardValidator;
    private final PraiseCommentValidator praiseCommentValidator;
    private final WriteDiaryValidator writeDiaryValidator;

    private final Map<QuestType, QuestValidator> validators = new EnumMap<>(QuestType.class);

    @PostConstruct
    public void init() {
        validators.put(QuestType.ATTENDANCE, attendanceValidator);
        validators.put(QuestType.QUEST_CLEAR, questClearValidator);
        validators.put(QuestType.MISSION_REWARD, missionRewardValidator);
        validators.put(QuestType.PRAISE_COMMENT, praiseCommentValidator);
        validators.put(QuestType.WRITE_DIARY, writeDiaryValidator);
    }

    public QuestValidator getValidator(QuestType type) {
        return validators.get(type);
    }
}