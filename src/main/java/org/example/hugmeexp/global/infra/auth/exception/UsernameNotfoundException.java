package org.example.hugmeexp.global.infra.auth.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class UsernameNotfoundException extends BaseCustomException {
    public UsernameNotfoundException() {
        super(HttpStatus.CONFLICT, "이미 존재하는 사용자입니다.", 409);
    }
}
