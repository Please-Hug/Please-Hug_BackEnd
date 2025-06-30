package org.example.hugmeexp.global.infra.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.global.common.response.Response;
import org.example.hugmeexp.global.infra.auth.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "인증 관련 API")
@Slf4j
@RestController
@RequiredArgsConstructor
public class LogoutController {

    private final AuthService authService;

    @Operation(summary = "로그아웃", description = "로그아웃 요청을 보냄. 로그아웃 성공시, 액세스 토큰과 리프레시 토큰을 블랙리스트 처리")
    @PostMapping("/api/logout")
    public ResponseEntity<Response<Void>> logout(
            @Parameter(description = "Bearer 액세스 토큰. 'Bearer {token}' 형식으로 전달", example = "Bearer eyJhbGci...")
            @RequestHeader(value = "Authorization") String authHeader) {
        // 액세스 토큰 추출
        String accessToken = authHeader.substring(7);
        authService.logout(accessToken);

        return ResponseEntity.ok(Response.<Void>builder()
                .message("로그아웃 되었습니다")
                .build());
    }
}
