package org.example.hugmeexp.domain.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hugmeexp.domain.user.enums.UserRole;

/**
 * 관리자 페이지 단일 회원 상세 조회 시 사용하는 응답 DTO
 */

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserInfoResponse {
    private Long id;
    private String username;
    private String profileImage;
    private String name;
    private String description;
    private String phoneNumber;
    private UserRole role;
    private int level;
    private int nextLevelTotalExp;
    private int currentTotalExp;
    private int point;
}