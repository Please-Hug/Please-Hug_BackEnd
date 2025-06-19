package org.example.hugmeexp.global.infra.auth.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class UsernameNotfoundException extends BaseCustomException {
    public UsernameNotfoundException() {
        super(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.", 404);
    }
}
