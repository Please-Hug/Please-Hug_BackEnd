package org.example.hugmeexp.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponse {
    private final String username;
    private final String name;
    private final String description;
    private final String ProfileImagePath;
}
