package org.example.hugmeexp.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.user.dto.response.UserInfoResponse;
import org.example.hugmeexp.domain.user.entity.User;
import org.example.hugmeexp.domain.user.mapper.UserResponseMapper;
import org.example.hugmeexp.domain.user.service.UserService;
import org.example.hugmeexp.global.common.response.Response;
import org.example.hugmeexp.global.security.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User", description = "User 관련 관련 API")
@Slf4j
@RestController
@RequiredArgsConstructor
public class UserInfoController {

    private final UserService userService;

    @Operation(summary = "로그인한 유저 상세 정보 조회", description = "유저 상세 정보 조회")
    @GetMapping("/api/v1/user")
    public ResponseEntity<Response<UserInfoResponse>> getUser(@AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();
        UserInfoResponse result = userService.getUserInfoResponse(user);

        return ResponseEntity.ok(Response.<UserInfoResponse>builder()
                .message("회원 정보를 불러왔습니다.")
                .data(result)
                .build());
    }
}
