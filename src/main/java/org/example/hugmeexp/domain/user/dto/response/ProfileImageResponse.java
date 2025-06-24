package org.example.hugmeexp.domain.user.dto.response;

import lombok.Getter;

@Getter
public class ProfileImageResponse {
    private final String profileImage;

    public ProfileImageResponse(String profileImage) {
        this.profileImage = profileImage;
    }
}
