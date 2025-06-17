package org.example.hugmeexp.global.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Entity
@Table(name = "profile_image")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProfileImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 36)
    private String uuid;

    @Column(nullable = false)
    private String path;

    @Column(nullable = false)
    private String extension;

    @Builder
    private ProfileImage(String path, String extension) {
        this.uuid = UUID.randomUUID().toString();
        this.path = path;
        this.extension = extension;
    }

    public static ProfileImage registerProfileImage(String path, String extension) {
        return ProfileImage.builder()
                .path(path)
                .extension(extension)
                .build();
    }
}
