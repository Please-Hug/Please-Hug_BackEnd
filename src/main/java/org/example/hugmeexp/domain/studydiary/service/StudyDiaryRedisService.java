package org.example.hugmeexp.domain.studydiary.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.studydiary.dto.response.StudyDiaryFindAllResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudyDiaryRedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String CACHE_KEY_PREFIX = "study_diary:popular:weekly:";
    private static final String WEEKLY_POPULAR_KEY = "sorted";
    private static final Duration CACHE_DURATION = Duration.ofMinutes(35);

    // 캐싱 로직
    public void cacheWeeklyPopularDiaries(List<StudyDiaryFindAllResponse> diaries) {
        //저장 방법은 크게 3개로 나뉨 : 전체 리스트 직렬화, Redis 리스트, Sorted Set
        //여기서는 Sorted Set을 활용
        String key = WEEKLY_POPULAR_KEY;

        // 기존 데이터 삭제
        redisTemplate.delete(CACHE_KEY_PREFIX + key);

        // Sorted Set에 저장 (score는 역순 인덱스)
        // 좋아요가 가장 많은 Dairy가 가장 높은 score
        Set<ZSetOperations.TypedTuple<Object>> tuples = new HashSet<>();
        for (int i = 0; i < diaries.size(); i++) {
            tuples.add(ZSetOperations.TypedTuple.of(diaries.get(i),
                    (double)(diaries.size() - i)));
        }

        redisTemplate.opsForZSet().add(CACHE_KEY_PREFIX + key, tuples);
        redisTemplate.expire(CACHE_KEY_PREFIX + key, CACHE_DURATION);
    }

    public Page<StudyDiaryFindAllResponse> getCachedWeeklyPopularDiaries(Pageable pageable) {
        // 캐시 조회 로직
        String key = CACHE_KEY_PREFIX + WEEKLY_POPULAR_KEY;
        
        // 전체 개수 조회, 주간 일기가 없을 시에 빈 페이지 return
        Long totalElements = redisTemplate.opsForZSet().size(key);
        if (totalElements == null || totalElements == 0) {
            return Page.empty(pageable);
        }
        
        // 페이지 계산
        long start = pageable.getOffset() * 10;
        long end = Math.min((start + 1) * 10,  totalElements);  //start부터 10개 or 전체 항목 갯수(max)
        
        // 해당 범위의 데이터 조회 (높은 점수부터)
        //LinkedHashSet return(Set인데 순서보장)
        Set<Object> results = redisTemplate.opsForZSet()
                .reverseRange(key, start, end);

        //혹시 모르니 한번 더 예외처리
        if (results == null || results.isEmpty()) {
            return Page.empty(pageable);
        }
        
        // Object를 StudyDiaryFindAllResponse로 변환
        List<StudyDiaryFindAllResponse> content = results.stream()
                .map(obj -> objectMapper.convertValue(obj, StudyDiaryFindAllResponse.class))
                .toList();
        
        // Page 객체 생성
        return new PageImpl<>(content, pageable, totalElements);
    }
}