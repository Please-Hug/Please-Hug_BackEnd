package org.example.hugmeexp.domain.qeust.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class QuestDeletedException extends BaseCustomException {
    public QuestDeletedException(){
        super(HttpStatus.BAD_REQUEST, "Already deleted quest.");
    }
}
