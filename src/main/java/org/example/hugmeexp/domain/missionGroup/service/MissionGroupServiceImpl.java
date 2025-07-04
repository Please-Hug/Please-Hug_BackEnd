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
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public List<MissionGroupResponse> getAllMissionGroups() {
        return missionGroupRepository.findAll()
                .stream()
                .map(missionGroupMapper::toMissionGroupResponse)
                .toList();
    }

    @Override
    @Transactional
    public MissionGroupResponse createMissionGroup(MissionGroupRequest request) {
        User teacher = userRepository.findByUsername(request.getTeacherUsername())
                .orElseThrow(TeacherNotFoundException::new);
        MissionGroup missionGroup = MissionGroup.builder()
                .teacher(teacher)
                .name(request.getName())
                .build();
        var savedMissionGroup = missionGroupRepository.save(missionGroup);
        return missionGroupMapper.toMissionGroupResponse(savedMissionGroup);
    }

    @Override
    public MissionGroupResponse getMissionById(Long id) {
        return missionGroupRepository.findById(id)
                .map(missionGroupMapper::toMissionGroupResponse)
                .orElseThrow(MissionGroupNotFoundException::new);
    }

    @Override
    @Transactional
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
    public void deleteMissionGroup(Long id) {
        if (!missionGroupRepository.existsById(id)) {
            throw new MissionGroupNotFoundException();
        }
        missionGroupRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void addUserToMissionGroup(Long userId, Long missionGroupId) {
        User user = userRepository.findById(userId)
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
    public void removeUserFromMissionGroup(Long userId, Long missionGroupId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        MissionGroup missionGroup = missionGroupRepository.findById(missionGroupId)
                .orElseThrow(MissionGroupNotFoundException::new);

        UserMissionGroup userMissionGroup = userMissionGroupRepository
                .findByUserAndMissionGroup(user, missionGroup)
                .orElseThrow(NotExistsUserMissionGroupException::new);

        userMissionGroupRepository.delete(userMissionGroup);
    }

    @Override
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
                .toList();
    }

    @Override
    public List<UserMissionGroupResponse> getMyMissionGroups(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);

        List<UserMissionGroup> userMissionGroups = userMissionGroupRepository.findByUserId(user.getId());
        return userMissionGroups
                .stream()
                .map(userMissionGroupMapper::toUserMissionGroupResponse)
                .toList();
    }

    @Override
    public List<UserProfileResponse> getUsersInMissionGroup(Long missionGroupId) {
        MissionGroup missionGroup = missionGroupRepository.findById(missionGroupId)
                .orElseThrow(MissionGroupNotFoundException::new);

        List<UserMissionGroup> userMissionGroups = userMissionGroupRepository.findAllByMissionGroup(missionGroup);
        return userMissionGroups.stream()
                .map(UserMissionGroup::getUser)
                .distinct()
                .map(user -> new UserProfileResponse(
                        user.getPublicProfileImageUrl(),
                        user.getUsername(),
                        user.getName()
                ))
                .toList();
    }
}
