package org.example.hugmeexp.domain.user.mapper;

import org.example.hugmeexp.domain.user.dto.response.ProfileImageResponse;
import org.example.hugmeexp.domain.user.dto.response.UserInfoResponse;
import org.example.hugmeexp.domain.user.dto.response.UserProfileResponse;
import org.example.hugmeexp.domain.user.entity.User;

public class UserResponseMapper {

    // UserInfoResponse DTO 리턴
    public static UserInfoResponse toUserInfoResponse(User user) {
        return new UserInfoResponse(
                user.getFullProfileImagePath(),
                user.getName(),
                user.getDescription(),
                user.getPhoneNumber()
        );
    }

    // UserProfileResponse DTO 리턴
    public static UserProfileResponse toUserProfileResponse(User user) {
        return new UserProfileResponse(
                user.getFullProfileImagePath(),
                user.getUsername(),
                user.getName()
        );
    }

    public static ProfileImageResponse toProfileImageResponse(User user) {
        return new ProfileImageResponse(
                user.getFullProfileImagePath()
        );
    }
}
