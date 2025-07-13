package org.example.hugmeexp.global.infra.auth.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.user.enums.UserRole;
import org.example.hugmeexp.global.infra.auth.exception.InvalidAccessTokenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
//@RequiredArgsConstructor
public class JwtTokenProvider {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String USED_TOKEN_PREFIX = "used_refresh_token:";

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    private SecretKey key;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    @Autowired
    public JwtTokenProvider(@Qualifier("customStringRedisTemplate") RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    // 액세스 토큰 생성
    public String createAccessToken(String username, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenExpiration);

        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(now)
                .claim("nbf", now.getTime() / 1000)
                .setExpiration(expiry)
                .signWith(key)
                .compact();
    }

    // 리프레시 토큰 생성
    public String createRefreshToken(String username, UserRole role) {
        String jti = UUID.randomUUID().toString();
        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshTokenExpiration);

        String refreshToken = Jwts.builder()
                .setId(jti)
                .setSubject(username) // 유저네임
                .claim("role", role)  // 권한
                .setIssuedAt(now)
                .claim("nbf", now.getTime() / 1000)
                .setExpiration(expiry)
                .signWith(key)
                .compact();

        // USED_TOKEN_PREFIX + jti를 false로 설정(지금 발급한 refresh token은 사용되지 않았음을 표시)
        redisTemplate.opsForValue().set(USED_TOKEN_PREFIX + jti, "false", Duration.ofMillis(refreshTokenExpiration));
        return refreshToken;
    }

    // 사용되지 않는 리프레시 토큰인지 확인
    public boolean isRefreshTokenRevoked(String refreshToken) {
        try {
            String jti = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(refreshToken)
                    .getBody()
                    .getId();

            // USED_TOKEN_PREFIX + jti 값이 true인지 검사
            String value = redisTemplate.opsForValue().get(USED_TOKEN_PREFIX + jti);
            return value == null || "true".equals(value);
        } catch (Exception e) {
            log.warn("An exception occurred while verifying the refresh token: {}", e.getMessage(), e);
            return true;
        }
    }

    // 리프레시 토큰 무효화 처리
    public void revokeRefreshToken(String refreshToken) {
        try {
            String jti = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(refreshToken)
                    .getBody()
                    .getId();

            long remaining = getTokenRemainingTimeMillis(refreshToken);

            // USED_TOKEN_PREFIX + jti 값을 true로 변경(기존 refresh token은 이미 한 번 사용되었음을 표시)
            redisTemplate.opsForValue().set(USED_TOKEN_PREFIX + jti, "true", Duration.ofMillis(remaining));
        } catch (Exception e) {
            log.error("Failed to revoke the refresh token: {}", e.getMessage(), e);
        }
    }

    // 토큰을 통해 username을 가져오는 메서드(액세스, 리프레시 둘다 사용 가능)
    public String getUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // 토큰을 통해 role을 가져오는 메서드(액세스, 리프레시 둘다 사용 가능)
    public String getRole(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }

    // 토큰이 유효한지 확인하는 메서드(액세스, 리프레시 둘다 사용 가능)
    public boolean validate(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    // 토큰의 만료시간을 조회하는 메서드,(액세스, 리프레시 둘다 사용 가능)
    public long getTokenRemainingTimeMillis(String token) {
        try {
            Date expiration = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();

            return Math.max(0, expiration.getTime() - System.currentTimeMillis());
        } catch (Exception e) {
            log.warn("Failed to extract expiration from token: {}", e.getMessage(), e);
            return 0;
        }
    }

    // 리프레시 토큰을 재발급 받을때 액세스 토큰을 검증하는 메서드
    public void validateAccessTokenForReissue(String accessToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken);

            // 액세스 토큰이 여전히 유효하다면 예외를 던짐
            //log.warn("Access token is still valid. Rejecting issuing refresh token - accessToken: {}...", accessToken.substring(0, 10));
//            throw new AccessTokenStillValidException();
        }
        catch (ExpiredJwtException e) {
            // 액세스 토큰의 유효기간이 만료되었다면 pass
        }
        catch (JwtException e) {
            // 형식 오류, 서명 오류, 위조라면 예외를 던짐
            log.warn("Failed to validate access token while issuing refresh token - accessToken: {}...", accessToken.substring(0, 10));
            throw new InvalidAccessTokenException();
        }
    }
}
