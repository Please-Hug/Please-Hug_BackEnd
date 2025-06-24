package org.example.hugmeexp.domain.qeust.exception;

import org.example.hugmeexp.domain.user.entity.User;
import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class NoSuchQuestException extends BaseCustomException {
    public NoSuchQuestException(User user, Long userQuestId) {
        super(HttpStatus.NOT_FOUND, user.getUsername() + " has not been assigned quest #" + userQuestId + ".");
    }
}
