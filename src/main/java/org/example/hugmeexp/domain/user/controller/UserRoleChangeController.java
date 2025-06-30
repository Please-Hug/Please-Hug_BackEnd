package org.example.hugmeexp.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.user.dto.request.ChangeRoleRequest;
import org.example.hugmeexp.domain.user.service.UserService;
import org.example.hugmeexp.global.common.response.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User", description = "User 관련 관련 API")
@Slf4j
@RestController
@RequiredArgsConstructor
public class UserRoleChangeController {

    private final UserService userService;

    @Operation(summary = "유저 권한 업데이트", description = "권한이 ADMIN인 유저가 요청해야 함.")
    @PostMapping("/api/v1/admin/change-role")
    public ResponseEntity<Response<Void>> changeRole(@Valid @RequestBody ChangeRoleRequest request) {
        userService.changeUserRole(request);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
