package org.example.hugmeexp.domain.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hugmeexp.domain.user.enums.UserRole;

/**
 * 관리자 페이지 회원 목록 전체 조회 시 사용하는 응답 DTO
 */

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserAllResponse {
    private Long id;
    private String profileImage;
    private String username;
    private String name;
    private UserRole role;
}