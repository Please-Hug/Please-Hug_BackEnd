package org.example.hugmeexp.domain.user.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BaseCustomException {
    public UserNotFoundException() {
        super(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.", 404);
    }
}
