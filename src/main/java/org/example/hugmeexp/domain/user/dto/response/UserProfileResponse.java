package org.example.hugmeexp.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserProfileResponse {
    private final String profileImagePath;
    private final String username;
    private final String name;
}
