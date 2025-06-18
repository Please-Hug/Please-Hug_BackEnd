package org.example.hugmeexp.global.infra.auth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.global.common.response.Response;
import org.example.hugmeexp.global.infra.auth.dto.request.RefreshRequest;
import org.example.hugmeexp.global.infra.auth.dto.response.AuthResponse;
import org.example.hugmeexp.global.infra.auth.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RefreshController
{
    private final AuthService authService;

    @PostMapping("/api/refresh")
    public ResponseEntity<Response<AuthResponse>> refresh(@RequestBody RefreshRequest request) {
        AuthResponse result = authService.refreshTokens(request.getRefreshToken());
        return ResponseEntity.ok(Response.<AuthResponse>builder()
                .message("토큰이 갱신되었습니다")
                .data(result)
                .build());
    }
}