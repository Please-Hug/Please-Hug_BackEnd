package org.example.hugmeexp.domain.user.controller;

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

@Slf4j
@RestController
@RequiredArgsConstructor
public class ProfileImageRegisterController {

    private final UserService userService;

    @PostMapping("/api/v1/profileImage")
    public ResponseEntity<Response<ProfileImageResponse>> registerProfileImage(
            @RequestPart("file") MultipartFile file, @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();
        String fullPath = userService.registerProfileImage(user, file);

        return ResponseEntity.ok(Response.<ProfileImageResponse>builder()
                .message("프로필 이미지가 등록되었습니다.")
                .data(new ProfileImageResponse(fullPath))
                .build());
    }
}
