package org.example.hugmeexp.domain.admin.service;

import org.example.hugmeexp.domain.admin.dto.request.RoleChangeRequest;
import org.example.hugmeexp.domain.user.dto.request.UserUpdateRequest;
import org.example.hugmeexp.domain.user.dto.response.UserInfoResponse;
import org.example.hugmeexp.domain.user.dto.response.UserProfileResponse;
import org.example.hugmeexp.domain.user.entity.User;
import org.example.hugmeexp.domain.user.service.UserService;
import org.example.hugmeexp.domain.user.mapper.UserResponseMapper;
import org.example.hugmeexp.domain.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public Page<UserProfileResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(UserResponseMapper::toUserProfileResponse);
    }

    @Transactional(readOnly = true)
    public UserInfoResponse getUserByAdmin(String username) {
        User user = userService.findByUsername(username);
        return userService.getUserInfoResponse(user);
    }

    @Transactional
    public UserInfoResponse updateUserByAdmin(String username, UserUpdateRequest request) {
        User target = userService.findByUsername(username);
        return userService.updateUserInfo(target, request);
    }

    @Transactional
    public void deleteUserByAdmin(String username) {
        userService.deleteByUsername(username);
    }

    @Transactional
    public UserInfoResponse changeUserRole(String username, RoleChangeRequest request) {
        User user = userService.findByUsername(username);
        user.changeRole(request.getRole());
        return userService.getUserInfoResponse(user);
    }
}