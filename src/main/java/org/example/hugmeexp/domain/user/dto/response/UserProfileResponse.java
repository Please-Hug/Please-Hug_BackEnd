package org.example.hugmeexp.domain.user.dto.response;

import lombok.Getter;

@Getter
public class UserProfileResponse {
    private final String profileImage;
    private final String username;
    private final String name;

    public UserProfileResponse(String profileImage, String username, String name) {
        this.profileImage = profileImage;
        this.username = username;
        this.name = name;
    }
}
