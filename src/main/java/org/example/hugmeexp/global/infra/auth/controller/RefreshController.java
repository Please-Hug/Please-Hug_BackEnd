package org.example.hugmeexp.global.infra.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Auth", description = "인증 관련 API")
@Slf4j
@RestController
@RequiredArgsConstructor
public class RefreshController
{
    private final AuthService authService;

    @Operation(summary = "액세스 토큰 재발급", description = "액세스 토큰을 재발급하는 요청을 보냄, 액세스 토큰의 유효기간이 지나야 요청 가능, 리프레시 성공시 액세스 토큰과 리프레시 토큰을 응답 함")
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