package org.example.hugmeexp.domain.qeust.validator;

import org.example.hugmeexp.domain.qeust.entity.UserQuest;

public interface QuestValidator {
    boolean isValid(UserQuest userQuest);
}
