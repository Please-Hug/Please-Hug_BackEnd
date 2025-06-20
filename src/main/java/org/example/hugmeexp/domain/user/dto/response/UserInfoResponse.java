package org.example.hugmeexp.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserInfoResponse {
    private final String profileImagePath;
    private final String name;
    private final String description;
    private final String phoneNumber;
}
