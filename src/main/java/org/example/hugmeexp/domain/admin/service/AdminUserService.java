package org.example.hugmeexp.domain.admin.service;

import org.example.hugmeexp.domain.admin.dto.request.RoleChangeRequest;
import org.example.hugmeexp.domain.admin.dto.response.AdminUserAllResponse;
import org.example.hugmeexp.domain.admin.dto.response.AdminUserInfoResponse;
import org.example.hugmeexp.domain.admin.mapper.AdminUserResponseMapper;
import org.example.hugmeexp.domain.user.dto.request.UserUpdateRequest;
import org.example.hugmeexp.domain.user.dto.response.UserInfoResponse;
import org.example.hugmeexp.domain.user.entity.User;
import org.example.hugmeexp.domain.user.service.UserService;
import org.example.hugmeexp.domain.user.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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

    /** 1) 회원 목록 페이징 조회 */
    @Transactional(readOnly = true)
    @Cacheable(value = "adminUserList", key = "#pageable.pageNumber + '::' + #pageable.pageSize + '::' + #pageable.sort.toString()")
    public Page<AdminUserAllResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(AdminUserResponseMapper::toProfileResponse);
    }

    /** 2) 단일 회원 상세 조회 */
    @Transactional(readOnly = true)
    @Cacheable(value = "adminUserInfo", key = "#username")
    public AdminUserInfoResponse getUserByAdmin(String username) {
        User u = userService.findByUsername(username);
        UserInfoResponse base = userService.getUserInfoResponse(u);
        return AdminUserResponseMapper.toInfoResponse(u, base);
    }

    /** 3) 회원 정보 수정 */
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "adminUserInfo", key = "#username"),
            @CacheEvict(value = "adminUserList", allEntries = true),
            @CacheEvict(value = "userInfo", key = "#username") // UserService 캐시도 무효화
    })
    public AdminUserInfoResponse updateUserByAdmin(String username, UserUpdateRequest req) {
        User u = userService.findByUsername(username);
        UserInfoResponse updatedBase = userService.updateUserInfo(u, req);
        return AdminUserResponseMapper.toInfoResponse(u, updatedBase);
    }

    /** 4) 회원 삭제 */
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "adminUserInfo", key = "#username"),
            @CacheEvict(value = "adminUserList", allEntries = true),
            @CacheEvict(value = "userInfo", key = "#username"),
            @CacheEvict(value = "attendanceStatus", key = "#username"),
            @CacheEvict(value = "attendanceDates", key = "#username")
    })
    public AdminUserInfoResponse deleteUserByAdmin(String username) {
        User u = userService.findByUsername(username);
        UserInfoResponse deletedBase = userService.getUserInfoResponse(u);
        AdminUserInfoResponse response = AdminUserResponseMapper.toInfoResponse(u, deletedBase);
        userService.deleteByUsername(username);
        return response;
    }

    /** 5) 권한 변경 */
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "adminUserInfo", key = "#username"),
            @CacheEvict(value = "adminUserList", allEntries = true),
            @CacheEvict(value = "userInfo", key = "#username") // UserService 캐시도 무효화
    })
    public AdminUserInfoResponse changeUserRole(String username, RoleChangeRequest req) {
        User u = userService.findByUsername(username);
        u.changeRole(req.getRole());
        UserInfoResponse base = userService.getUserInfoResponse(u);
        return AdminUserResponseMapper.toInfoResponse(u, base);
    }
}