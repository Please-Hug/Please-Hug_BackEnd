package org.example.hugmeexp.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
//@AllArgsConstructor
public class UserProfileResponse {
    private final String profileImage;
    private final String username;
    private final String name;
    public UserProfileResponse(String username, String name, String profileImage) {
        this.username = username;
        this.name = name;
        this.profileImage = profileImage;
    }
}
