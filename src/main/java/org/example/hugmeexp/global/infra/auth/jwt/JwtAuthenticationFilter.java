package org.example.hugmeexp.global.infra.auth.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.global.common.exception.response.ErrorResponse;
import org.example.hugmeexp.global.infra.auth.service.RedisSessionService;
import org.example.hugmeexp.global.security.CustomUserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtProvider;
    private final RedisSessionService redisService;
    private final ObjectMapper objectMapper;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        // 액세스 토큰 추출
        if (header != null && header.startsWith("Bearer ")) {
            String accessToken = header.substring(7);

            /*
                블랙리스트 확인
                만약 블랙리스트로 등록된 엑세스 토큰을 제시하면 요청을 거부 (로그아웃 등으로 무효화된 토큰은 거부)
            */
            if (redisService.isAccessTokenBlacklisted(accessToken)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");

                ErrorResponse errorResponse = ErrorResponse.builder()
                        .code(401)
                        .message("Revoked token")
                        .build();

                String json = objectMapper.writeValueAsString(errorResponse);
                response.getWriter().write(json);
                log.warn("Access attempt with revoked token - accessToken: {}...", accessToken.substring(0, 10));
                return;
            }

            /*
                엑세스 토큰 유효성 검사
                - 서명, 만료시간, 포맷 등 검증
                - 유효하다면 사용자 정보를 조회하여 SecurityContext에 등록
            */
            if (jwtProvider.validate(accessToken)) {
                String username = jwtProvider.getUsername(accessToken);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                /*
                    principal: 로그인 주체 → userDetails
                    credentials: 패스워드 (JWT 인증에선 의미 없음) → null
                    authorities: 사용자의 권한 목록 → ROLE_USER, ROLE_ADMIN 등
                */
                Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(auth);
            }
            else log.warn("Failed to validate token - accessToken: {}", accessToken.substring(0, 10) + "...");
        }

        chain.doFilter(request, response);
    }
}