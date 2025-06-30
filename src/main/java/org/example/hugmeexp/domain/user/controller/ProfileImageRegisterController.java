package org.example.hugmeexp.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.user.dto.response.ProfileImageResponse;
import org.example.hugmeexp.domain.user.entity.User;
import org.example.hugmeexp.domain.user.service.UserService;
import org.example.hugmeexp.global.common.response.Response;
import org.example.hugmeexp.global.security.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "User", description = "User 관련 관련 API")
@Slf4j
@RestController
@RequiredArgsConstructor
public class ProfileImageRegisterController {

    private final UserService userService;

    @Operation(summary = "유저 프로필 이미지 등록/변경", description = "프로필 이미지 등록/변경")
    @PostMapping("/api/v1/profileImage")
    public ResponseEntity<Response<ProfileImageResponse>> registerProfileImage(
            @Parameter(description = "업로드할 프로필 이미지 파일", required = true) @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();
        ProfileImageResponse result = userService.registerProfileImage(user, file);

        return ResponseEntity.ok(Response.<ProfileImageResponse>builder()
                .message("프로필 이미지가 등록되었습니다.")
                .data(result)
                .build());
    }
}
