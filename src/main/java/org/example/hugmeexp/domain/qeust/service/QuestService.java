package org.example.hugmeexp.domain.qeust.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.qeust.dto.UserQuestResponse;
import org.example.hugmeexp.domain.qeust.entity.UserQuest;
import org.example.hugmeexp.domain.qeust.enums.QuestType;
import org.example.hugmeexp.domain.qeust.exception.NoSuchQuestException;
import org.example.hugmeexp.domain.qeust.mapper.UserQuestMapper;
import org.example.hugmeexp.domain.qeust.repository.UserQuestRepository;
import org.example.hugmeexp.domain.qeust.validator.QuestValidator;
import org.example.hugmeexp.domain.qeust.validator.QuestValidatorFactory;
import org.example.hugmeexp.domain.user.entity.User;
import org.example.hugmeexp.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestService {

    private final UserQuestRepository userQuestRepository;
    private final UserRepository userRepository;

    private final QuestValidatorFactory questValidatorFactory;
    private final UserQuestMapper userQuestMapper;

    /**
     * 사용자에게 할당된 퀘스트 조회 메서드
     * @param user
     * @return
     */
    public List<UserQuestResponse> getAllQuests(User user) {

        // 사용자에게 할당된 퀘스트 조회
        List<UserQuest> userQuests = userQuestRepository.findAllByUser(user);

        for (UserQuest userQuest : userQuests) {
            checkCondition(userQuest);
        }

        return userQuests.stream()
                .map(userQuestMapper::toResponse)
                .collect(Collectors.toList());
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

    private void checkCondition(UserQuest userQuest) {
        if (userQuest.isCompleted() || userQuest.isCompletable()) return;

        QuestType type = userQuest.getQuest().getType();
        QuestValidator validator = questValidatorFactory.getValidator(type);
        if (validator != null && validator.isValid(userQuest)) {
            userQuest.makeCompletable();
            userQuestRepository.save(userQuest);
        }
    }
}
