package org.example.hugmeexp.global.infra.auth.jwt;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.example.hugmeexp.global.entity.enumeration.UserRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
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

        String token = Jwts.builder()
                .setId(jti)
                .setSubject(username) // 유저네임
                .claim("role", role)  // 권한
                .setIssuedAt(now)
                .claim("nbf", now.getTime() / 1000)
                .setExpiration(expiry)
                .signWith(key)
                .compact();

        redisTemplate.opsForValue()
                .set(USED_TOKEN_PREFIX + jti, "false", Duration.ofMillis(refreshTokenExpiration));
        return token;
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

            String value = redisTemplate.opsForValue().get(USED_TOKEN_PREFIX + jti);
            return value == null || "true".equals(value);
        } catch (Exception e) {
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
            redisTemplate.opsForValue()
                    .set(USED_TOKEN_PREFIX + jti, "true", Duration.ofMillis(remaining));
        } catch (Exception ignored) {}
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
            return 0;
        }
    }
}
