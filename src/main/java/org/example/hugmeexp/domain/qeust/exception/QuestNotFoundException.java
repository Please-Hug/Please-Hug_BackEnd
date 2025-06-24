package org.example.hugmeexp.domain.qeust.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class QuestNotFoundException extends BaseCustomException {
    public QuestNotFoundException(Long questId) {
        super(HttpStatus.NOT_FOUND, "There is no quest with ID: " + questId);
    }
}
