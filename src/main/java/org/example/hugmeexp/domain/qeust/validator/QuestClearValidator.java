package org.example.hugmeexp.domain.qeust.validator;

import lombok.RequiredArgsConstructor;
import org.example.hugmeexp.domain.qeust.entity.UserQuest;
import org.example.hugmeexp.domain.qeust.repository.UserQuestRepository;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class QuestClearValidator implements QuestValidator {

    private final UserQuestRepository userQuestRepository;

    @Override
    public boolean isValid(UserQuest userQuest) {
        return userQuestRepository.findAllByUser(userQuest.getUser()).stream()
                .anyMatch(UserQuest::isCompleted);
    }
}