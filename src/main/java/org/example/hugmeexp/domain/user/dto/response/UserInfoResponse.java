package org.example.hugmeexp.domain.user.dto.response;

import lombok.Getter;
import org.example.hugmeexp.domain.user.enums.UserRole;

@Getter
public class UserInfoResponse {

    private final String profileImage;
    private final String name;
    private final String description;
    private final String phoneNumber;
    private final UserRole role;
    private final int level;
    private final int nextLevelTotalExp;
    private final int currentTotalExp;
    private final int point;

    public UserInfoResponse(String profileImage, String name, String description, String phoneNumber, UserRole role, int level, int nextLevelTotalExp, int currentTotalExp, int point) {
        this.profileImage = profileImage;
        this.name = name;
        this.description = description;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.level = level;
        this.nextLevelTotalExp = nextLevelTotalExp;
        this.currentTotalExp = currentTotalExp;
        this.point = point;
    }
}
