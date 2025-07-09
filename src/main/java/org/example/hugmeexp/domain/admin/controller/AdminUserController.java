// src/main/java/org/example/hugmeexp/domain/admin/controller/AdminUserController.java
package org.example.hugmeexp.domain.admin.controller;

import org.example.hugmeexp.domain.admin.dto.request.RoleChangeRequest;
import org.example.hugmeexp.domain.admin.dto.response.AdminUserAllResponse;
import org.example.hugmeexp.domain.admin.dto.response.AdminUserInfoResponse;
import org.example.hugmeexp.domain.admin.service.AdminUserService;
import org.example.hugmeexp.domain.user.dto.request.UserUpdateRequest;
import org.example.hugmeexp.domain.user.dto.response.UserInfoResponse;
import org.example.hugmeexp.domain.user.dto.response.UserProfileResponse;
import org.example.hugmeexp.global.common.response.Response;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@Tag(name = "Admin", description = "관리자 전용 사용자 관리 API")
@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @Operation(summary = "회원 목록 조회", description = "회원 목록을 페이징하여 전체 조회")
    @GetMapping
    public ResponseEntity<Response<Page<AdminUserAllResponse>>> listUsers(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<AdminUserAllResponse> page = adminUserService.getAllUsers(pageable);
        return ResponseEntity.ok(Response.<Page<AdminUserAllResponse>>builder()
                .message("회원 목록을 조회했습니다.")
                .data(page)
                .build());
    }

    @Operation(summary = "회원 단일 조회", description = "특정 회원의 상세 정보를 조회")
    @GetMapping("/{username}")
    public ResponseEntity<Response<AdminUserInfoResponse>> getUser(
            @PathVariable String username) { // @AuthenticationPrincipal은 지금 요청을 보낸 admin 계정의 username과 같은 값이 필요 없으므로 사용 x
        AdminUserInfoResponse user = adminUserService.getUserByAdmin(username);
        return ResponseEntity.ok(Response.<AdminUserInfoResponse>builder()
                .message("회원 정보를 조회했습니다.")
                .data(user)
                .build());
    }

    @Operation(summary = "회원 정보 수정", description = "특정 회원의 정보를 수정")
    @PatchMapping("/{username}")
    public ResponseEntity<Response<AdminUserInfoResponse>> updateUser(
            @PathVariable String username,
            @Valid @RequestBody UserUpdateRequest request) {
        AdminUserInfoResponse updated = adminUserService.updateUserByAdmin(username, request);
        return ResponseEntity.ok(Response.<AdminUserInfoResponse>builder()
                .message("회원 정보가 수정되었습니다.")
                .data(updated)
                .build());
    }

    @Operation(summary = "회원 삭제", description = "특정 회원을 삭제")
    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteUser(@PathVariable String username) {
        adminUserService.deleteUserByAdmin(username);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "권한 변경", description = "특정 회원의 역할을 변경")
    @PatchMapping("/{username}/role")
    public ResponseEntity<Response<AdminUserInfoResponse>> changeUserRole(
            @PathVariable String username,
            @Valid @RequestBody RoleChangeRequest request) {
        AdminUserInfoResponse result = adminUserService.changeUserRole(username, request);
        return ResponseEntity.ok(Response.<AdminUserInfoResponse>builder()
                .message("회원 권한이 변경되었습니다.")
                .data(result)
                .build());
    }
}