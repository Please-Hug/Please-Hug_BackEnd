package org.example.hugmeexp.domain.studydiary.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.studydiary.dto.response.StudyDiaryFindAllResponse;
import org.example.hugmeexp.domain.studydiary.entity.StudyDiary;
import org.example.hugmeexp.domain.studydiary.repository.StudyDiaryRepository;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class StudyDiarySchedulingConfig {
    private final StudyDiaryService studyDiaryService;
    private final StudyDiaryRepository studyDiaryRepository;
    private final StudyDiaryRedisService studyDiaryRedisService;

    @PostConstruct
    public void init() {
        log.info("==StudyDiarySchedulingConfig== initialized");
    }

    @Scheduled(fixedRate = 1800000) // 30분 = 1800000ms
    public void cacheTodayPopularStudyDiaries() {
        log.info("caching weekly studyDiaries");

        // 이번 주 월요일~일요일 범위 계산
        LocalDateTime startOfWeek = LocalDateTime.now().with(DayOfWeek.MONDAY).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfWeek = startOfWeek.plusDays(6).withHour(23).withMinute(59).withSecond(59);

        //일주일간 인기 배움일기 50개 조회
        List<StudyDiary> studyDiaries = studyDiaryRepository.findWeeklyPopularStudyDiaries(startOfWeek, endOfWeek);

        //response로 전환
        List<StudyDiaryFindAllResponse> studyDiaryFindAllResponsePage = studyDiaries.stream().map(studyDiary -> {
            return StudyDiaryFindAllResponse.builder()
                    .id(studyDiary.getId())
                    .name(studyDiary.getUser().getName())
                    .title(studyDiary.getTitle())
                    .content(studyDiary.getContent())
                    .likeNum(studyDiary.getLikeCount())
                    .commentNum(studyDiary.getComments().size())
                    .createdAt(studyDiary.getCreatedAt())
                    .build();
        }).toList();

        studyDiaryRedisService.cacheWeeklyPopularDiaries(studyDiaryFindAllResponsePage);
    }
}