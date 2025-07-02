package org.example.hugmeexp.domain.user.mapper;

import org.example.hugmeexp.domain.user.entity.ProfileImage;
import org.springframework.stereotype.Component;

@Component
public class ProfileImageMapper {
    public String map(ProfileImage profileImage) {
        if (profileImage == null) {
            return null;
        }
        return "/profile-images/" + profileImage.getUuid() + profileImage.getExtension();
    }
}
