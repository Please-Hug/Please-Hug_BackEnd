package org.example.hugmeexp.domain.admin.mapper;

import org.example.hugmeexp.domain.admin.dto.response.AdminUserAllResponse;
import org.example.hugmeexp.domain.admin.dto.response.AdminUserInfoResponse;
import org.example.hugmeexp.domain.user.dto.response.UserInfoResponse;
import org.example.hugmeexp.domain.user.entity.User;

public class AdminUserResponseMapper {

    /** 목록 조회용 매핑 */
    public static AdminUserAllResponse toProfileResponse(User u) {
        return new AdminUserAllResponse(
                u.getId(),
                u.getPublicProfileImageUrl() != null ? u.getPublicProfileImageUrl() : null,
                u.getUsername(),
                u.getName(),
                u.getRole()
        );
    }

    /** 상세 조회용 매핑 */
    public static AdminUserInfoResponse toInfoResponse(User u, UserInfoResponse base) {
        return new AdminUserInfoResponse(
                u.getId(),
                u.getUsername(),
                base.getProfileImage() != null ? base.getProfileImage() : null,
                base.getName(),
                base.getDescription(),
                base.getPhoneNumber(),
                u.getRole(),
                base.getLevel(),
                base.getNextLevelTotalExp(),
                base.getCurrentTotalExp(),
                base.getPoint()
        );
    }
}