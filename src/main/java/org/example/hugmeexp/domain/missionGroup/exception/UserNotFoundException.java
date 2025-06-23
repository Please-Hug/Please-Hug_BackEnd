package org.example.hugmeexp.domain.missionGroup.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BaseCustomException {
    public UserNotFoundException() {
        super(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다.");
    }
}
