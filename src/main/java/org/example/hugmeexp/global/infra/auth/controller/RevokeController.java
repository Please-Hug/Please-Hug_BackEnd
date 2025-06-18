package org.example.hugmeexp.global.infra.auth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.global.common.response.Response;
import org.example.hugmeexp.global.infra.auth.dto.request.RevokeTokenRequest;
import org.example.hugmeexp.global.infra.auth.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RevokeController
{
    private final AuthService authService;

    // 토큰 무효화 API (토큰 탈취 대응)
    @PostMapping("/api/v1/admin/revoke-token")
    public ResponseEntity<Response<Void>> revokeAccessToken(@RequestBody RevokeTokenRequest request)
    {
        authService.revokeAccessToken(request.getToken());

        return ResponseEntity.ok(Response.<Void>builder()
                .message("토큰이 무효화되었습니다")
                .build());
    }
}