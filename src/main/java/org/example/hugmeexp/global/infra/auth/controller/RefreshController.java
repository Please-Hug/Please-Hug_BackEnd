package org.example.hugmeexp.global.infra.auth.controller;

import jakarta.validation.Valid;
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
    public ResponseEntity<Response<AuthResponse>> refresh(@Valid @RequestBody RefreshRequest request) {
        // 서비스에 비즈니스 로직 위임
        AuthResponse result = authService.refreshTokens(request);

        return ResponseEntity.ok(Response.<AuthResponse>builder()
                .message("리프레시 토큰이 갱신되었습니다")
                .data(result)
                .build());
    }
}