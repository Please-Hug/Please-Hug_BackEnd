package org.example.hugmeexp.global.infra.auth.controller;

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

@Slf4j
@RestController
@RequiredArgsConstructor
public class LoginController {

    private final AuthService authService;

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