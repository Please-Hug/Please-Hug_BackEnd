package org.example.hugmeexp.domain.qeust.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class QuestNotCompletableException extends BaseCustomException {
    public QuestNotCompletableException() {
        super(HttpStatus.BAD_REQUEST, "This quest is not completable.");
    }
}
