package org.example.hugmeexp.domain.studydiary.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class UserNotFoundForStudyDiaryException extends BaseCustomException {

    public UserNotFoundForStudyDiaryException() {
        super(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다.", 3001);
    }
}
