package org.example.hugmeexp.domain.user.dto.response;

import lombok.Getter;

@Getter
public class UserInfoResponse {
    private final String profileImage;
    private final String name;
    private final String description;
    private final String phoneNumber;

    public UserInfoResponse(String profileImage, String name, String description, String phoneNumber) {
        this.profileImage = profileImage;
        this.name = name;
        this.description = description;
        this.phoneNumber = phoneNumber;
    }
}
