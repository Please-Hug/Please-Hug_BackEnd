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
    public ResponseEntity<Response<Void>> logout(@RequestHeader(value = "Authorization") String authHeader) {
        // 토큰 추출
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String accessToken = authHeader.substring(7);

            // 로그아웃 요청이 성공했는지 실패했는지를 클라이언트에게 상세히 알려주지 않는 것이 좋음
            authService.logout(accessToken);
        }

        return ResponseEntity.ok(Response.<Void>builder()
                .message("로그아웃 되었습니다")
                .build());
    }
}
