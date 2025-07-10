package org.example.hugmeexp.domain.studydiary.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.example.hugmeexp.global.infra.auth.service.RedisSessionService;
import org.example.hugmeexp.global.infra.auth.service.AuthService;
import org.example.hugmeexp.global.infra.auth.service.TokenService;
import org.example.hugmeexp.global.infra.auth.jwt.JwtTokenProvider;

import static org.mockito.Mockito.*;

/**
 * StudyDiary 테스트를 위한 전용 설정
 * 
 * JWT 토큰 관리 및 Redis 세션 관리를 위한 Mock 객체들을 제공합니다.
 * @BeforeEach에서 초기화할 수 있는 유틸리티 메서드들을 포함합니다.
 */
@TestConfiguration
public class StudyDiaryTestConfig {
    
    /**
     * JWT 토큰 프로바이더 Mock
     * 테스트에서 JWT 토큰 생성/검증을 위한 Mock
     */
    @Bean
    @Primary
    public JwtTokenProvider jwtTokenProvider() {
        JwtTokenProvider mockProvider = mock(JwtTokenProvider.class);
        
        // 기본적인 JWT 토큰 관련 Mock 설정
        when(mockProvider.validate(anyString())).thenReturn(true);
        when(mockProvider.getUsername(anyString())).thenReturn("testuser");
        when(mockProvider.getRole(anyString())).thenReturn("USER");
        when(mockProvider.createAccessToken(anyString(), anyString())).thenReturn("mock-access-token");
        when(mockProvider.createRefreshToken(anyString(), any())).thenReturn("mock-refresh-token");
        
        return mockProvider;
    }
    
    /**
     * Redis 세션 서비스 Mock
     * JWT 토큰 블랙리스트 관리를 위한 Mock
     */
    @Bean
    @Primary
    public RedisSessionService redisSessionService() {
        RedisSessionService mockService = mock(RedisSessionService.class);
        
        // 기본적인 Redis 세션 관련 Mock 설정
        when(mockService.isAccessTokenBlacklisted(anyString())).thenReturn(false);
        doNothing().when(mockService).blacklistAccessToken(anyString(), anyLong());
        
        return mockService;
    }
    
    /**
     * 인증 서비스 Mock
     * 사용자 로그인/로그아웃 관련 Mock
     */
    @Bean
    @Primary
    public AuthService authService() {
        AuthService mockService = mock(AuthService.class);
        
        // 기본적인 인증 관련 Mock 설정 (필요시 테스트에서 추가 설정)
        return mockService;
    }
    
    /**
     * 테스트용 Redis Mock 초기화 유틸리티
     * @BeforeEach에서 호출하여 각 테스트마다 Redis Mock 상태를 초기화
     */
    public static void initializeRedisMocks(StringRedisTemplate stringRedisTemplate,
                                          RedisSessionService redisSessionService) {
        // StringRedisTemplate Mock 초기화
        reset(stringRedisTemplate);
        ValueOperations<String, String> valueOps = mock(ValueOperations.class);
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOps);
        when(stringRedisTemplate.hasKey(anyString())).thenReturn(false);
        
        // RedisSessionService Mock 초기화
        reset(redisSessionService);
        when(redisSessionService.isAccessTokenBlacklisted(anyString())).thenReturn(false);
        doNothing().when(redisSessionService).blacklistAccessToken(anyString(), anyLong());
    }
    
    /**
     * JWT 토큰 프로바이더 Mock 초기화 유틸리티
     * @BeforeEach에서 호출하여 JWT 관련 Mock 상태를 초기화
     */
    public static void initializeJwtMocks(JwtTokenProvider jwtTokenProvider) {
        reset(jwtTokenProvider);
        when(jwtTokenProvider.validate(anyString())).thenReturn(true);
        when(jwtTokenProvider.getUsername(anyString())).thenReturn("testuser");
        when(jwtTokenProvider.getRole(anyString())).thenReturn("USER");
        when(jwtTokenProvider.createAccessToken(anyString(), anyString())).thenReturn("mock-access-token");
        when(jwtTokenProvider.createRefreshToken(anyString(), any())).thenReturn("mock-refresh-token");
    }
}