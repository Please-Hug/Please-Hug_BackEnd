package org.example.hugmeexp.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.example.hugmeexp.domain.user.dto.request.UserUpdateRequest;
import org.example.hugmeexp.domain.user.dto.response.UserInfoResponse;
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

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

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
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // phoneNumber를 바탕으로 User 리턴
    public Optional<User> findByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    // 경험치 증가
    @Transactional
    public void increaseExp(User user, int value) {
        // @AuthenticationPrincipal로 받은 User 객체는 영속 상태가 아니므로 영속성 컨텍스트에 넣어야 함.
        User findUser = findById(user.getId());

        findUser.increaseExp(value);
    }

    // 구름조각 증가
    @Transactional
    public void increasePoint(User user, int value) {
        // @AuthenticationPrincipal로 받은 User 객체는 영속 상태가 아니므로 영속성 컨텍스트에 넣어야 함.
        User findUser = findById(user.getId());

        findUser.increasePoint(value);
    }

    // 구름조각 감소
    @Transactional
    public void decreasePoint(User user, int value) {
        // @AuthenticationPrincipal로 받은 User 객체는 영속 상태가 아니므로 영속성 컨텍스트에 넣어야 함.
        User findUser = findById(user.getId());

        findUser.decreasePoint(value);
    }

    @Transactional
    public UserInfoResponse updateUserInfo(User user, UserUpdateRequest request) {

        // @AuthenticationPrincipal로 받은 User 객체는 영속 상태가 아니므로 영속성 컨텍스트에 넣어야 함.
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
        return UserResponseMapper.toUserInfoResponse(findUser);
    }

    // 프로필 이미지 등록
    @Transactional
    public String registerProfileImage(User user, MultipartFile file) {
        User findUser = findById(user.getId());

        // 프로젝트 루트 경로
        String projectPath = System.getProperty("user.dir");
        String directoryPath = projectPath + "/profile-images/";

        // 디렉토리 생성
        File directory = new File(directoryPath);
        if (!directory.exists() && !directory.mkdirs()) {
            throw new RuntimeException("Failed to create the /profile-images/ directory.");
        }

        // 기존 이미지 삭제
        if (findUser.isRegisterProfileImage()) {
            String oldPath = findUser.getFullProfileImagePath();
            File oldFile = new File(oldPath);
            if (oldFile.exists() && !oldFile.delete()) {
                throw new RuntimeException("Failed to delete the existing profile image.");
            }
        }

        // 이미지 등록
        String extension = extractExtension(file.getOriginalFilename());
        findUser.registerProfileImage(directoryPath, extension);
        String newAbsolutePath = findUser.getFullProfileImagePath();

        // 파일 저장
        try {
            file.transferTo(new File(newAbsolutePath));
        } catch (IOException e) {
            throw new RuntimeException("Failed to save the profile image.", e);
        }

        return newAbsolutePath;
    }

    // username을 바탕으로 삭제
    @Transactional
    public void deleteByUsername(String username){
        long deletedCount = userRepository.deleteByUsername(username);
        if(deletedCount == 0) throw new UserNotFoundException();
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

}
