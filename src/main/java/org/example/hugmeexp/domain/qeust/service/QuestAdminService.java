package org.example.hugmeexp.domain.qeust.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.qeust.dto.QuestRequest;
import org.example.hugmeexp.domain.qeust.dto.QuestResponse;
import org.example.hugmeexp.domain.qeust.dto.UserQuestResponse;
import org.example.hugmeexp.domain.qeust.entity.Quest;
import org.example.hugmeexp.domain.qeust.entity.UserQuest;
import org.example.hugmeexp.domain.qeust.enums.QuestType;
import org.example.hugmeexp.domain.qeust.exception.QuestDeletedException;
import org.example.hugmeexp.domain.qeust.exception.QuestNotFoundException;
import org.example.hugmeexp.domain.qeust.exception.UserNotFoundInQuestException;
import org.example.hugmeexp.domain.qeust.mapper.QuestMapper;
import org.example.hugmeexp.domain.qeust.mapper.UserQuestMapper;
import org.example.hugmeexp.domain.qeust.repository.QuestRepository;
import org.example.hugmeexp.domain.qeust.repository.UserQuestRepository;
import org.example.hugmeexp.domain.shop.exception.ProductDeletedException;
import org.example.hugmeexp.domain.user.entity.User;
import org.example.hugmeexp.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestAdminService {

    private final QuestRepository questRepository;
    private final UserRepository userRepository;
    private final UserQuestRepository userQuestRepository;

    private final QuestMapper questMapper;
    private final UserQuestMapper userQuestMapper;

    /**
     * 퀘스트 생성 메서드
     * @param request
     * @return
     */
    @Transactional
    public QuestResponse createQuest(QuestRequest request) {

        Quest quest = questMapper.toEntity(request);
        log.info("변환된 Quest Entity:{}", quest.getName());

        questRepository.save(quest);

        // 응답 DTO로 매핑하여 반환
        return questMapper.toResponse(quest);
    }

    /**
     * 퀘스트 삭제 메서드
     * @param questId
     */
    @Transactional
    public void deleteQuest(Long questId) {

        // Id와 일치하는 퀘스트가 없다면 예외 처리
        Quest quest = questRepository.findById(questId)
                .orElseThrow(()  -> new QuestNotFoundException(questId));

        log.info("Attempting to delete product ID: {}", questId);
        quest.delete();
        questRepository.save(quest);
        log.info("Product successfully deleted.");
    }

    /**
     * 퀘스트 수정 메서드
     * @param questId
     * @param request
     * @return
     */
    @Transactional
    public QuestResponse modifyQuest(Long questId, QuestRequest request) {

        // Id와 일치하는 퀘스트가 없다면 예외 처리
        Quest quest = questRepository.findById(questId)
                .orElseThrow(()  -> new QuestNotFoundException(questId));

        // 이미 삭제된 퀘스트이면 예외 처리
        if (quest.isDeleted()) {
            throw new QuestDeletedException();
        }

        // 수정된 퀘스트를 응답 DTO로 매핑하여 반환
        quest.updateQuest(request);
        Quest modifiedQuest = questRepository.save(quest);
        return questMapper.toResponse(modifiedQuest);
    }

    /**
     * 유저 퀘스트 할당 메서드
     * - username으로 조회한 유저에게 모든 퀘스트 할당
     * - 이미 퀘스트가 할당된 유저라면 모두 삭제 후 초기화
     * @param username
     * @return
     */
    @Transactional
    public List<UserQuestResponse> assignQuest(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundInQuestException(username));

        // 이미 존재하는 퀘스트는 전부 삭제
        List<UserQuest> allByUser = userQuestRepository.findAllByUser(user);
        userQuestRepository.deleteAll(allByUser);

        // 해당 사용자에게 모든 퀘스트를 할당 후 응답 DTO로 변환하여 반환
        List<UserQuestResponse> response = new ArrayList<>();
        List<Quest> allQuests = questRepository.findAllByIsDeletedFalse();
        for (Quest quest : allQuests) {
            UserQuest userQuest = UserQuest.createUserQuest(user, quest);
            UserQuest savedUserQuest = userQuestRepository.save(userQuest);
            response.add(userQuestMapper.toResponse(savedUserQuest));
        }

        return response;
    }

    /**
     * 모든 퀘스트 진행 상황 초기화
     */
    @Transactional
    public void resetQuest() {

        List<UserQuest> allUserQuests = userQuestRepository.findAll();
        for (UserQuest allUserQuest : allUserQuests) {
            allUserQuest.reset();
        }
    }

    // ===== 테스트용 or 사용하지 않는 메서드 =====
    // 퀘스트 일괄 생성, init.sql 추가로 더 이상 사용하지 않음
    public void initQuest() {
        QuestRequest[] requests = new QuestRequest[]{
                new QuestRequest("출석체크 하기", "http://출석체크", QuestType.ATTENDANCE),
                new QuestRequest("일일 퀘스트 완료하기", "http://퀘스트", QuestType.QUEST_CLEAR),
                new QuestRequest("미션 리워드 받기", "http://미션", QuestType.MISSION_REWARD),
                new QuestRequest("배움일기 작성하기", "http://배움일기", QuestType.WRITE_DIARY),
                new QuestRequest("칭찬 댓글달기", "http://칭찬", QuestType.PRAISE_COMMENT)
        };

        for (QuestRequest request : requests) {
            questRepository.save(questMapper.toEntity(request));
        }
    }
}
