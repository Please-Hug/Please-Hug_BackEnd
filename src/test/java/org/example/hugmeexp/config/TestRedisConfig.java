package org.example.hugmeexp.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;

import static org.mockito.Mockito.*;

/**
 * 테스트 전용 Redis Mock 설정
 * 
 * 실제 Redis 서버 없이 테스트를 실행하기 위한 Mock 객체들을 제공합니다.
 * @TestConfiguration으로 테스트 환경에서만 활성화됩니다.
 */
@TestConfiguration
public class TestRedisConfig {
    
    /**
     * Redis 연결 팩토리 Mock
     * 실제 Redis 서버 연결 없이 테스트에서 사용할 Mock 객체
     */
    @Bean
    @Primary
    public RedisConnectionFactory redisConnectionFactory() {
        return mock(RedisConnectionFactory.class);
    }
    
    /**
     * String Redis Template Mock
     * JWT 토큰 블랙리스트 관리 등에 사용되는 StringRedisTemplate의 Mock
     */
    @Bean
    @Primary
    public StringRedisTemplate stringRedisTemplate() {
        StringRedisTemplate mockTemplate = mock(StringRedisTemplate.class);
        
        // 기본적인 Redis 연산들을 Mock으로 설정
        when(mockTemplate.hasKey(anyString())).thenReturn(false);
        when(mockTemplate.opsForValue()).thenReturn(mock(org.springframework.data.redis.core.ValueOperations.class));
        when(mockTemplate.opsForSet()).thenReturn(mock(org.springframework.data.redis.core.SetOperations.class));
        when(mockTemplate.opsForHash()).thenReturn(mock(org.springframework.data.redis.core.HashOperations.class));
        
        return mockTemplate;
    }
    
    /**
     * Redis Template Mock
     * 일반적인 Redis 작업을 위한 RedisTemplate Mock
     */
    @Bean
    @Primary
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> mockTemplate = mock(RedisTemplate.class);
        
        // 기본적인 Redis 연산들을 Mock으로 설정
        when(mockTemplate.hasKey(anyString())).thenReturn(false);
        when(mockTemplate.opsForValue()).thenReturn(mock(org.springframework.data.redis.core.ValueOperations.class));
        when(mockTemplate.opsForSet()).thenReturn(mock(org.springframework.data.redis.core.SetOperations.class));
        when(mockTemplate.opsForHash()).thenReturn(mock(org.springframework.data.redis.core.HashOperations.class));
        
        return mockTemplate;
    }
}