package org.example.hugmeexp.domain.qeust.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.qeust.dto.UserQuestResponse;
import org.example.hugmeexp.domain.qeust.entity.UserQuest;
import org.example.hugmeexp.domain.qeust.exception.NoSuchQuestException;
import org.example.hugmeexp.domain.qeust.mapper.UserQuestMapper;
import org.example.hugmeexp.domain.qeust.repository.UserQuestRepository;
import org.example.hugmeexp.domain.user.entity.User;
import org.example.hugmeexp.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestService {

    private final UserQuestRepository userQuestRepository;
    private final UserRepository userRepository;

    private final UserQuestMapper userQuestMapper;

    /**
     * 사용자에게 할당된 퀘스트 조회 메서드
     * @param user
     * @return
     */
    public List<UserQuestResponse> getAllQuests(User user) {

        // 사용자에게 할당된 퀘스트 조회
        List<UserQuest> allByUser = userQuestRepository.findAllByUser(user);

        // 응답 DTO로 변환하여 반환
        List<UserQuestResponse> response = new ArrayList<>();
        for (UserQuest userQuest : allByUser) {
            response.add(userQuestMapper.toResponse(userQuest));
        }

        return response;
    }

    /**
     * 퀘스트 완료 메서드
     * @param user
     * @param userQuestId
     * @return
     */
    @Transactional
    public UserQuestResponse completeQuest(User user, Long userQuestId) {

        // 사용자가 완료하려는 userQuestId가 할당된 퀘스트인지 확인
        UserQuest userQuest = userQuestRepository.findByUserAndId(user, userQuestId)
                .orElseThrow(() -> new NoSuchQuestException(user, userQuestId));

        // 퀘스트 완료 후 응답 DTO로 변환하여 반환 및 사용자의 포인트 증가
        userQuest.complete();
        User completeUser = userQuest.getUser();
        completeUser.increasePoint(50);
        completeUser.increaseExp(10);
        userRepository.save(completeUser);
        UserQuest savedUserQuest = userQuestRepository.save(userQuest);
        return userQuestMapper.toResponse(savedUserQuest);
    }
}
