package org.example.hugmeexp.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.user.dto.request.UserUpdateRequest;
import org.example.hugmeexp.domain.user.dto.response.UserInfoResponse;
import org.example.hugmeexp.domain.user.entity.User;
import org.example.hugmeexp.domain.user.service.UserService;
import org.example.hugmeexp.global.common.response.Response;
import org.example.hugmeexp.global.security.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User", description = "User 관련 관련 API")
@Slf4j
@RestController
@RequiredArgsConstructor
public class UserUpdateController {

    private final UserService userService;

    @Operation(summary = "유저 정보 수정", description = "유저 상세 정보 수정(이름, 소개, 전화번호)")
    @PatchMapping("/api/v1/user")
    public ResponseEntity<Response<?>> updateUserInfo(@Valid @RequestBody UserUpdateRequest request, @AuthenticationPrincipal CustomUserDetails userDetails)
    {
        User user = userDetails.getUser();

        // 서비스에 비즈니스 로직 위임
        UserInfoResponse result = userService.updateUserInfo(user, request);

        return ResponseEntity.ok(Response.builder()
                .message("회원 정보가 업데이트 되었습니다.")
                .data(result)
                .build());
    }
}
