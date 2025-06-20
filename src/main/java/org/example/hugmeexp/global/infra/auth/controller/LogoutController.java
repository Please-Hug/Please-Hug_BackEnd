package org.example.hugmeexp.global.infra.auth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.global.common.response.Response;
import org.example.hugmeexp.global.infra.auth.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class LogoutController {

    private final AuthService authService;

    @PostMapping("/api/logout")
    public ResponseEntity<Response<?>> logout(@RequestHeader(value = "Authorization") String authHeader) {
        // 액세스 토큰 추출
        String accessToken = authHeader.substring(7);
        authService.logout(accessToken);

        return ResponseEntity.ok(Response.builder()
                .message("로그아웃 되었습니다")
                .build());
    }
}
