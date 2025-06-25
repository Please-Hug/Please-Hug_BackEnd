package org.example.hugmeexp.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.user.enums.UserRole;
import org.example.hugmeexp.domain.user.exception.InvalidValueException;
import org.example.hugmeexp.global.entity.BaseEntity;

import static jakarta.persistence.CascadeType.*;

@Slf4j
@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
        User와 ProfileImage는 1:1 관계, User -> ProfileImage 단방향 매핑
        PERSIST: user 객체 저장 시, ProfileImage도 같이 저장
        REMOVE: user 객체 삭제 시, profileImage도 같이 삭제
        orphanRemoval = true: User.profileImage = null와 같이 참조가 끊기면 ProfileImage 엔티티 삭제
        FetchType.LAZY: 지연로딩
    */
    @OneToOne(cascade = {PERSIST, REMOVE}, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_image_id", nullable = true)
    private ProfileImage profileImage;

    @Column(nullable = false, length = 32, unique = true) // 32글자, 유니크 제약조건
    private String username;

    @Column(nullable = false, length = 60) // bcrypt 암호화 위해서 비밀번호 60글자
    private String password;

    @Column(nullable = false, length = 32)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(nullable = false)
    private Integer point;

    @Column(nullable = false)
    private Integer exp;

    @Column(nullable = true)
    private String description;

    @Column(nullable = false, length = 13, unique = true)
    private String phoneNumber;

    @Builder
    private User(String username, String password, String name, String phoneNumber) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.role = UserRole.USER; // role은 기본으로 USER
        this.point = 0;
        this.exp = 0;
        this.phoneNumber = phoneNumber;
    }

    // 정적 팩터리
    public static User createUser(String username, String encodedPassword, String name, String phoneNumber) {
        return User.builder()
                .username(username)
                .password(encodedPassword)
                .name(name)
                .phoneNumber(phoneNumber)
                .build();
    }

    // 회원정보 변경
    public void updateUserInfo(String name, String description, String phoneNumber) {
        this.name = name;
        this.description = description;
        this.phoneNumber = phoneNumber;
    }

    // UserRole 변경
    public void changeRole(UserRole role) {
        this.role = role;
    }

    // 구름조각 증가
    public void increasePoint(int value) {
        if(value <= 0) throw new InvalidValueException("양수만 요청할 수 있습니다.");
        log.info("point increase - user: {}({}) {} -> {}", this.username, this.name, this.point, this.point + value);
        this.point += value;
    }

    // 구름조각 감소
    public void decreasePoint(int value) {
        if(value <= 0) throw new InvalidValueException("양수만 요청할 수 있습니다.");
        if(this.point - value < 0) throw new InvalidValueException("구름조각은 음수가 될 수 없습니다.");
        log.info("point decrease - user: {}({}) {} -> {}", this.username, this.name, this.point, this.point - value);
        this.point -= value;
    }

    // 경험치 증가
    public void increaseExp(int value) {
        if(value <= 0) throw new InvalidValueException("양수만 요청할 수 있습니다.");
        log.info("exp increase - user: {}({}) {} -> {}", this.username, this.name, this.exp, this.exp + value);
        this.exp += value;
    }

    // 비밀번호 변경(서비스 계층에서 암호화된 패스워드가 넘어와야 함)
    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    // 소개글 변경
    public void changeDescription(String description) {
        this.description = description;
    }

    // 전화번호 변경
    public void changePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /*
        1. 이미 프로필 이미지가 있는 경우
        - 서비스 계층에서 기존 이미지를 스토리지에서 삭제
        - registerProfileImage 호출

        2. 기존에 프로필 이미지가 없는 경우
        - registerProfileImage 호출
    */
    public void registerProfileImage(String path, String extension) {
        this.profileImage = ProfileImage.registerProfileImage(path, extension);
    }

    // 프로필 이미지가 이미 등록되어있는지 확인하는 메서드
    public boolean isRegisterProfileImage() {
        return this.profileImage != null;
    }

    // ProfileImage 연관관계를 끊는 메서드
    public void deleteProfileImage() {
        this.profileImage = null;
    }

    // 프로필 이미지의 전체 경로를 리턴하는 메서드
    public String getStoredProfileImagePath() {
        return (profileImage != null) ? profileImage.getPath() + profileImage.getUuid() + profileImage.getExtension() : null;
    }

    public String getPublicProfileImageUrl() {
        if (profileImage == null) return null;

        String internalPath = profileImage.getPath();
        String uuid = profileImage.getUuid();
        String ext = profileImage.getExtension();

        String fullPath = internalPath + uuid + ext;

        // "/application" 제거
        if (fullPath.startsWith("/application")) {
            return fullPath.substring("/application".length());
        }

        return fullPath;
    }
}
