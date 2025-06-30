package org.example.hugmeexp.global.infra.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.global.common.response.Response;
import org.example.hugmeexp.global.infra.auth.dto.request.LoginRequest;
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
public class LoginController {

    private final AuthService authService;

    @Operation(summary = "로그인", description = "로그인 요청을 보냄. 로그인 성공시, 액세스 토큰과 리프레시 토큰을 응답함.")
    @PostMapping("/api/login")
    public ResponseEntity<Response<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        // 서비스에 비즈니스 로직 위임
        AuthResponse result = authService.login(request);

        return ResponseEntity.ok(Response.<AuthResponse>builder()
                .message("로그인에 성공했습니다")
                .data(result)
                .build());
    }
}