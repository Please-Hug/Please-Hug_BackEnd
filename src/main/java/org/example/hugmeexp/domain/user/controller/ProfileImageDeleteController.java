package org.example.hugmeexp.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.user.entity.User;
import org.example.hugmeexp.domain.user.service.UserService;
import org.example.hugmeexp.global.security.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User", description = "User 관련 관련 API")
@Slf4j
@RestController
@RequiredArgsConstructor
public class ProfileImageDeleteController {

    private final UserService userService;

    @Operation(summary = "유저 프로필 이미지 삭제", description = "프로필 이미지 삭제")
    @DeleteMapping("/api/v1/profileImage")
    public ResponseEntity<Void> deleteProfileImage(@AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();
        userService.deleteProfileImage(user);

        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
