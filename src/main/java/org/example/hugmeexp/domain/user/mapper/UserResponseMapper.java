package org.example.hugmeexp.domain.user.mapper;

import org.example.hugmeexp.domain.user.dto.response.ProfileImageResponse;
import org.example.hugmeexp.domain.user.dto.response.UserInfoResponse;
import org.example.hugmeexp.domain.user.dto.response.UserProfileResponse;
import org.example.hugmeexp.domain.user.entity.User;

public class UserResponseMapper {

    // UserInfoResponse DTO 리턴
    public static UserInfoResponse toUserInfoResponse(User user, int level, int nextLevelTotalExp) {
        return new UserInfoResponse(
                user.getPublicProfileImageUrl(),
                user.getName(),
                user.getDescription(),
                user.getPhoneNumber(),
                level,
                nextLevelTotalExp,
                user.getExp(),
                user.getPoint()
        );
    }

    // AdminUserAllResponse DTO 리턴
    public static UserProfileResponse toUserProfileResponse(User user) {
        return new UserProfileResponse(
                user.getPublicProfileImageUrl(),
                user.getUsername(),
                user.getName()
        );
    }

    // ProfileImageResponse DTO 반환
    public static ProfileImageResponse toProfileImageResponse(User user) {
        return new ProfileImageResponse(
                user.getPublicProfileImageUrl()
        );
    }

    // 프로필 이미지 요청 경로 반환
    public static String toPublicProfileImageUrl(User user) {
        return user.getPublicProfileImageUrl();
    }
}
