package org.example.hugmeexp.global.common.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class CacheService {

    private final StringRedisTemplate redisTemplate;

    public CacheService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 특정 사용자 관련 캐시만 무효화
    public void evictUserCache(String username) {
        String pattern = "userMissionByUsernameAndMissionGroup::" + username + "_*";
        evictCacheByPattern(pattern);
    }

    // 특정 미션 그룹 관련 캐시만 무효화
    public void evictMissionGroupCache(Long missionGroupId) {
        String pattern = "userMissionByUsernameAndMissionGroup::*_" + missionGroupId;
        evictCacheByPattern(pattern);
    }

    // 패턴과 일치하는 모든 키 삭제
    private void evictCacheByPattern(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}