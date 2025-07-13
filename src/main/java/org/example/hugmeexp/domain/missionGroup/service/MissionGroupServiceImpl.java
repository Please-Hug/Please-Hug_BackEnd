package org.example.hugmeexp.domain.missionGroup.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.hugmeexp.domain.mission.dto.response.UserMissionResponse;
import org.example.hugmeexp.domain.mission.mapper.UserMissionMapper;
import org.example.hugmeexp.domain.mission.repository.UserMissionRepository;
import org.example.hugmeexp.domain.missionGroup.dto.request.MissionGroupRequest;
import org.example.hugmeexp.domain.missionGroup.dto.response.MissionGroupResponse;
import org.example.hugmeexp.domain.missionGroup.dto.response.UserMissionGroupResponse;
import org.example.hugmeexp.domain.missionGroup.entity.MissionGroup;
import org.example.hugmeexp.domain.missionGroup.entity.UserMissionGroup;
import org.example.hugmeexp.domain.missionGroup.exception.*;
import org.example.hugmeexp.domain.missionGroup.mapper.MissionGroupMapper;
import org.example.hugmeexp.domain.missionGroup.mapper.UserMissionGroupMapper;
import org.example.hugmeexp.domain.missionGroup.repository.MissionGroupRepository;
import org.example.hugmeexp.domain.missionGroup.repository.UserMissionGroupRepository;
import org.example.hugmeexp.domain.user.dto.response.UserProfileResponse;
import org.example.hugmeexp.domain.user.repository.UserRepository;
import org.example.hugmeexp.domain.user.entity.User;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MissionGroupServiceImpl implements MissionGroupService {
    private final MissionGroupRepository missionGroupRepository;
    private final UserMissionGroupRepository userMissionGroupRepository;
    private final UserRepository userRepository;
    private final UserMissionRepository userMissionRepository;
    private final MissionGroupMapper missionGroupMapper;
    private final UserMissionMapper userMissionMapper;
    private final UserMissionGroupMapper userMissionGroupMapper;
    private final CacheManager cacheManager;


    @Override
    @Cacheable(value = "allMissionGroups")
    public List<MissionGroupResponse> getAllMissionGroups() {
        return missionGroupRepository.findAllWithTeacher()
                .stream()
                .map(missionGroupMapper::toMissionGroupResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = {"myMissionGroups", "allMissionGroups"}, allEntries = true)
    })
    public MissionGroupResponse createMissionGroup(MissionGroupRequest request, String username) {
        User teacher = userRepository.findByUsername(request.getTeacherUsername())
                .orElseThrow(TeacherNotFoundException::new);
        MissionGroup missionGroup = MissionGroup.builder()
                .teacher(teacher)
                .name(request.getName())
                .build();
        var savedMissionGroup = missionGroupRepository.save(missionGroup);

        UserMissionGroup userMissionGroup = UserMissionGroup.builder()
                .user(teacher)
                .missionGroup(savedMissionGroup)
                .build();
        userMissionGroupRepository.save(userMissionGroup);

        User creator = userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);
        UserMissionGroup creatorMissionGroup = UserMissionGroup.builder()
                .user(creator)
                .missionGroup(savedMissionGroup)
                .build();
        userMissionGroupRepository.save(creatorMissionGroup);
        try { // 생성 시점에서는 캐시가 없음. 다만 혹시 모를 캐시 존재 가능성에 대한 처리
            cacheManager.getCache("missionGroupById").evict(savedMissionGroup.getId());
        } catch (NullPointerException ignored) { }
        return missionGroupMapper.toMissionGroupResponse(savedMissionGroup);
    }

    @Override
    @Cacheable(value = "missionGroupById", key = "#id")
    public List<MissionGroupResponse> getMissionGroupById(Long id) {
        MissionGroupResponse dto = missionGroupRepository.findByIdWithTeacher(id)
                .map(missionGroupMapper::toMissionGroupResponse)
                .orElseThrow(MissionGroupNotFoundException::new);
        return new ArrayList<>(Arrays.asList(dto));
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = {"myMissionGroups", "allMissionGroups"}, allEntries = true),
            @CacheEvict(value = "missionGroupById", key = "#id")
    })
    public MissionGroupResponse updateMissionGroup(Long id, MissionGroupRequest request) {
        MissionGroup missionGroup = missionGroupRepository.findById(id)
                .orElseThrow(MissionGroupNotFoundException::new);

        User teacher = userRepository.findByUsername(request.getTeacherUsername())
                .orElseThrow(TeacherNotFoundException::new);

        missionGroup = missionGroup.toBuilder()
                .teacher(teacher)
                .name(request.getName())
                .build();
        var updatedMissionGroup = missionGroupRepository.save(missionGroup);

        return missionGroupMapper.toMissionGroupResponse(updatedMissionGroup);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = {"myMissionGroups", "allMissionGroups"}, allEntries = true),
            @CacheEvict(value = "missionGroupById", key = "#id"),
            @CacheEvict(value = "usersInMissionGroup", key = "#id"),
            @CacheEvict(value = "userMissionByUsernameAndMissionGroup", allEntries = true)
    })
    public void deleteMissionGroup(Long id) {
        if (!missionGroupRepository.existsById(id)) {
            throw new MissionGroupNotFoundException();
        }
        missionGroupRepository.deleteById(id);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "myMissionGroups", key = "#username"),
            @CacheEvict(value = "usersInMissionGroup", key = "#missionGroupId"),
            @CacheEvict(value = "userMissionByUsernameAndMissionGroup", key = "#username + '_' + #missionGroupId"),
    })
    public void addUserToMissionGroup(String username, Long missionGroupId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);
        MissionGroup missionGroup = missionGroupRepository.findById(missionGroupId)
                .orElseThrow(MissionGroupNotFoundException::new);

        if (userMissionGroupRepository.existsByUserAndMissionGroup(user, missionGroup)) {
            throw new AlreadyExistsUserMissionGroupException();
        }

        var userMissionGroup = UserMissionGroup.builder()
                .user(user)
                .missionGroup(missionGroup)
                .build();

        userMissionGroupRepository.save(userMissionGroup);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "myMissionGroups", key = "#username"),
            @CacheEvict(value = "usersInMissionGroup", key = "#missionGroupId"),
            @CacheEvict(value = "userMissionByUsernameAndMissionGroup", key = "#username + '_' + #missionGroupId"),
    })
    public void removeUserFromMissionGroup(String username, Long missionGroupId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);

        MissionGroup missionGroup = missionGroupRepository.findById(missionGroupId)
                .orElseThrow(MissionGroupNotFoundException::new);

        UserMissionGroup userMissionGroup = userMissionGroupRepository
                .findByUserAndMissionGroup(user, missionGroup)
                .orElseThrow(NotExistsUserMissionGroupException::new);

        userMissionGroupRepository.delete(userMissionGroup);
    }

    @Override
    @Cacheable(value = "userMissionByUsernameAndMissionGroup", key = "#username + '_' + #missionGroupId")
    public List<UserMissionResponse> findUserMissionByUsernameAndMissionGroup(String username, Long missionGroupId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);

        MissionGroup missionGroup = missionGroupRepository.findById(missionGroupId)
                .orElseThrow(MissionGroupNotFoundException::new);

        UserMissionGroup userMissionGroup = userMissionGroupRepository.findByUserAndMissionGroup(user, missionGroup)
                .orElseThrow(UserMissionGroupNotFoundException::new);

        return userMissionRepository.findByUserAndUserMissionGroup(user, userMissionGroup)
                .stream()
                .map(userMissionMapper::toUserMissionResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "myMissionGroups", key = "#username")
    public List<UserMissionGroupResponse> getMyMissionGroups(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);

        List<UserMissionGroup> userMissionGroups = userMissionGroupRepository.findByUserIdWithTeacher(user.getId());
        return userMissionGroups
                .stream()
                .map(userMissionGroupMapper::toUserMissionGroupResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "usersInMissionGroup", key = "#missionGroupId")
    public List<UserProfileResponse> getUsersInMissionGroup(Long missionGroupId) {
        MissionGroup missionGroup = missionGroupRepository.findById(missionGroupId)
                .orElseThrow(MissionGroupNotFoundException::new);

        List<User> users = userMissionGroupRepository.findUsersByMissionGroup(missionGroup);
        return users.stream()
                .map(user -> new UserProfileResponse(
                        user.getPublicProfileImageUrl(),
                        user.getUsername(),
                        user.getName()
                ))
                .collect(Collectors.toList());
    }
}
