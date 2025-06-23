package org.example.hugmeexp.domain.user.mapper;

import org.example.hugmeexp.domain.user.entity.ProfileImage;
import org.springframework.stereotype.Component;

@Component
public class ProfileImageMapper {
    public String map(ProfileImage profileImage) {
        if (profileImage == null) {
            return null;
        }
        return profileImage.getPath() + profileImage.getUuid() + profileImage.getExtension();
    }
}
