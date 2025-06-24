package org.example.hugmeexp.domain.qeust.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class UserNotFoundInQuestException extends BaseCustomException {
    public UserNotFoundInQuestException(String username) {
        super(HttpStatus.NOT_FOUND, "There is no user with username " + username);
    }
}
