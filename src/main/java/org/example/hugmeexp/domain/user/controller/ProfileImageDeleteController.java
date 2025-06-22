package org.example.hugmeexp.domain.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.user.entity.User;
import org.example.hugmeexp.domain.user.service.UserService;
import org.example.hugmeexp.global.security.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ProfileImageDeleteController {

    private final UserService userService;

    @DeleteMapping("/api/v1/profileImage")
    public ResponseEntity<Void> deleteProfileImage(@AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();
        userService.deleteProfileImage(user);

        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
