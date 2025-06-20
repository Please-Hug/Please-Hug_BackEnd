package org.example.hugmeexp.global.infra.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.global.common.response.Response;
import org.example.hugmeexp.global.infra.auth.dto.request.RegisterRequest;
import org.example.hugmeexp.global.infra.auth.dto.response.AuthResponse;
import org.example.hugmeexp.global.infra.auth.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RegisterController
{
    private final AuthService authService;

    @PostMapping("/api/register")
    public ResponseEntity<Response<AuthResponse>> register(@Valid @RequestBody RegisterRequest request)
    {
        // 서비스에 비즈니스 로직 위임
        AuthResponse result = authService.registerAndAuthenticate(request);

        return ResponseEntity.ok(Response.<AuthResponse>builder()
                .message("회원가입에 성공했습니다")
                .data(result)
                .build());
    }
}