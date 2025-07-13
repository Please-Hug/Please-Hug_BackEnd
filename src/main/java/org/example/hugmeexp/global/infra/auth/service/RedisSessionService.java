package org.example.hugmeexp.global.infra.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisSessionService
{
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String BLACKLIST_PREFIX = "blacklist:";

    // 블랙리스트에 토큰 추가 (토큰의 남은 유효시간만큼 블랙리스트에 보관)
    public void blacklistAccessToken(String accessToken, long remainingTimeMillis) {
        redisTemplate.opsForValue().set(BLACKLIST_PREFIX + accessToken, "REVOKED", Duration.ofMillis(remainingTimeMillis));
    }

    // 블랙리스트 확인
    public boolean isAccessTokenBlacklisted(String accessToken) {
        return redisTemplate.hasKey(BLACKLIST_PREFIX + accessToken);
    }
}