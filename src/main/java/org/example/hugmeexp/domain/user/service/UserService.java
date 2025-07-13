package org.example.hugmeexp.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.notification.service.NotificationService;
import org.example.hugmeexp.domain.user.dto.request.UserUpdateRequest;
import org.example.hugmeexp.domain.user.dto.response.ProfileImageResponse;
import org.example.hugmeexp.domain.user.dto.response.UserInfoResponse;
import org.example.hugmeexp.domain.user.dto.response.UserRankResponse;
import org.example.hugmeexp.domain.user.exception.*;
import org.example.hugmeexp.domain.user.mapper.UserResponseMapper;
import org.example.hugmeexp.domain.user.repository.UserRepository;
import org.example.hugmeexp.domain.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final NotificationService notificationService;

    private static final int MAX_LEVEL = 50;
    private static final int MAX_EXP = 122_500;

    // 모든 User 리턴
    public List<User> findAll() {
        return userRepository.findAll();
    }

    // userId를 바탕으로 유저 리턴
    public User findById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    // name을 바탕으로 모든 User 리턴
    public List<User> findByNameContaining(String name) {
        return userRepository.findByNameContaining(name);
    }

    // username을 바탕으로 User 리턴
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);
    }

    // phoneNumber를 바탕으로 User 리턴
    public Optional<User> findByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    // 경험치 증가
    @Transactional
    public void increaseExp(User user, int value) {
        log.info("increaseExp 진입 = user:{}, value: {}", user.getUsername(), value );
        User findUser = findById(user.getId());

        int oldLevel = calculateLevel(findUser.getExp());// 변경 전 레벨

        findUser.increaseExp(value);    // 경험치 증가
        int newLevel = calculateLevel(findUser.getExp());    // 변경 후 레벨

        if(newLevel > oldLevel) {
            notificationService.sendLevelUpNotification(findUser, newLevel, findUser.getId()); // 알림 전송
            userRepository.save(findUser);
        }
    }

    // 구름조각 증가
    @Transactional
    public void increasePoint(User user, int value) {
        User findUser = findById(user.getId());
        findUser.increasePoint(value);
    }

    // 구름조각 감소
    @Transactional
    public void decreasePoint(User user, int value) {
        User findUser = findById(user.getId());
        findUser.decreasePoint(value);
    }

    // User 종합 정보 조회
    public UserInfoResponse getUserInfoResponse(User user) {
        User findUser = findById(user.getId());

        int level = calculateLevel(findUser.getExp());
        int nextLevelTotalExp = getNextLevelTotalExp(findUser.getExp());
        return UserResponseMapper.toUserInfoResponse(findUser, level, nextLevelTotalExp);
    }

    // 회원 정보 업데이트
    @Transactional
    public UserInfoResponse updateUserInfo(User user, UserUpdateRequest request) {
        User findUser = findById(user.getId());

        // phoneNumber 중복 예외 처리
        String newPhoneNumber = request.getPhoneNumber();
        if (!newPhoneNumber.equals(findUser.getPhoneNumber())) {
            if (userRepository.existsByPhoneNumber(newPhoneNumber)) {
                throw new PhoneNumberDuplicatedException();
            }
        }

        // User 업데이트 및 결과 리턴
        findUser.updateUserInfo(request.getName(), request.getDescription(), request.getPhoneNumber());

        int level = calculateLevel(findUser.getExp());
        int nextLevelTotalExp = getNextLevelTotalExp(findUser.getExp());
        return UserResponseMapper.toUserInfoResponse(findUser, level, nextLevelTotalExp);
    }

    // 프로필 이미지 등록
    @Transactional
    public ProfileImageResponse registerProfileImage(User user, MultipartFile file) {
        log.info("Profile image update requested - user: {} ({})", user.getUsername(), user.getName());
        User findUser = findById(user.getId());

        // 프로젝트 디렉터리 하위 /profile-images/을 프로필 이미지 디렉터리로 설정
        String projectPath = System.getProperty("user.dir");
        String directoryPath = projectPath + "/profile-images/";

        // 프로필 이미지 디렉터리가 없다면 생성
        createDirectoryIfNotExists(directoryPath);

        // 기존 프로필 이미지가 있다면 삭제
        deleteExistingProfileImage(findUser);

        // 확장자 추출 및 프로필 이미지 엔티티 등록
        String extension = extractExtension(file.getOriginalFilename());
        findUser.registerProfileImage(directoryPath, extension);

        // 스토리지에 프로필 이미지 저장
        String absolutePath = findUser.getStoredProfileImagePath();
        saveFile(file, absolutePath);
        log.info("Profile image updated successfully - user: {} ({}) / image path: {}", user.getUsername(), user.getName(), absolutePath);

        // 프로필 이미지 경로 리턴
        return UserResponseMapper.toProfileImageResponse(findUser);
    }

    // 프로필 이미지 삭제
    @Transactional
    public void deleteProfileImage(User user) {
        User findUser = findById(user.getId());

        if (!findUser.isRegisterProfileImage()) {
            throw new ProfileImageNotFoundException();
        }

        // 기존 이미지 파일 경로
        String imagePath = findUser.getStoredProfileImagePath();
        File imageFile = new File(imagePath);

        // 이미지 삭제
        if (imageFile.exists() && !imageFile.delete()) {
            throw new RuntimeException("Failed to delete profile image from storage.");
        }

        // 연관 엔티티 끊기, orphanRemoval로 인해 DB에서 삭제됨
        findUser.deleteProfileImage();

        log.info("Profile image deleted successfully - user: {} ({}) / image path: {}", findUser.getUsername(), findUser.getName(), imagePath);
    }

    // username을 바탕으로 삭제
    @Transactional
    public void deleteByUsername(String username){
        long deletedCount = userRepository.deleteByUsername(username);
        if(deletedCount == 0) throw new UserNotFoundException();
    }

    public List<UserRankResponse> getUserRankings() {
        List<User> users = userRepository.findAllByOrderByExpDesc();
        return users.stream()
                .map(user -> {
                    int level = calculateLevel(user.getExp());
                    int rank = users.indexOf(user) + 1; // 1부터 시작하는 랭킹
                    return UserRankResponse.from(user, rank, level);
                })
                .toList();
    }

    // 프로필 이미지 확장자 추출
    private String extractExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            throw new InvalidValueException("유효하지 않은 파일명입니다.");
        }

        String ext = filename.substring(filename.lastIndexOf("."));
        if (!List.of(".jpg", ".jpeg", ".png", ".webp").contains(ext.toLowerCase())) {
            throw new UnsupportedImageExtensionException(ext);
        }

        return ext.toLowerCase();
    }

    // 프로필 이미지 디렉터리가 없다면 생성
    private void createDirectoryIfNotExists(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists() && !directory.mkdirs()) {
            throw new RuntimeException("Failed to create the /profile-images/ directory.");
        }
    }

    // 기존 프로필 이미지가 있다면 삭제
    private void deleteExistingProfileImage(User user) {
        if (!user.isRegisterProfileImage()) return;

        String oldPath = user.getStoredProfileImagePath();
        File oldFile = new File(oldPath);

        if (oldFile.exists() && !oldFile.delete()) {
            throw new RuntimeException("Failed to delete profile image from storage.");
        }

        log.info("Previous profile image deleted successfully - user: {} ({}) / image path: {}", user.getUsername(), user.getName(), oldFile);
    }

    // 스토리지에 프로필 이미지 저장
    private void saveFile(MultipartFile file, String absolutePath) {
        try {
            file.transferTo(new File(absolutePath));
        } catch (IOException e) {
            throw new RuntimeException("Failed to save the profile image.", e);
        }
    }

    private int calculateLevel(int exp) {
        if (exp >= MAX_EXP) return MAX_LEVEL;
        return (int) ((1 + Math.sqrt(1 + 0.08 * exp)) / 2.0);
    }

    private int getNextLevelTotalExp(int exp) {
        int currentLevel = calculateLevel(exp);

        if (currentLevel >= MAX_LEVEL) {
            return MAX_EXP;
        }

        int nextLevel = currentLevel + 1;
        return (100 * (nextLevel - 1) * nextLevel) / 2;
    }
}
