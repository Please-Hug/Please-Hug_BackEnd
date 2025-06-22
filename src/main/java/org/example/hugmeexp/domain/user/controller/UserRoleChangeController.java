package org.example.hugmeexp.domain.user.controller;

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

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserRoleChangeController {

    private final UserService userService;

    @PostMapping("/api/v1/admin/change-role")
    public ResponseEntity<Response<Void>> changeRole(@Valid @RequestBody ChangeRoleRequest request) {
        userService.changeUserRole(request);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
