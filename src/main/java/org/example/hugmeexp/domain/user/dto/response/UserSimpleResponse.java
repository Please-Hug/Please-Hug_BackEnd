package org.example.hugmeexp.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserSimpleResponse {
    private final String username;
    private final String name;
    private final String ProfileImagePath;
}
