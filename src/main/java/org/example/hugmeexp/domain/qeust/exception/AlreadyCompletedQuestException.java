package org.example.hugmeexp.domain.qeust.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class AlreadyCompletedQuestException extends BaseCustomException {
    public AlreadyCompletedQuestException(Long userQuestId) {
        super(HttpStatus.BAD_REQUEST, "Already completed quest.");
    }
}
