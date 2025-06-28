package org.example.hugmeexp.domain.qeust.validator;

import lombok.RequiredArgsConstructor;
import org.example.hugmeexp.domain.qeust.entity.UserQuest;
import org.example.hugmeexp.domain.studydiary.entity.StudyDiary;
import org.example.hugmeexp.domain.studydiary.repository.StudyDiaryRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Component
public class WriteDiaryValidator implements QuestValidator {

    private final StudyDiaryRepository studyDiaryRepository;

    @Override
    public boolean isValid(UserQuest userQuest) {
        Long userId = userQuest.getUser().getId();

        // 오늘의 시작과 끝 시간 계산
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        List<StudyDiary> diaries = studyDiaryRepository
                .findByUserIdAndCreatedAtBetween(userId, startOfDay, endOfDay);

        return !diaries.isEmpty();
    }
}