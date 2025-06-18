package org.example.hugmeexp.global.infra.auth.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class LoginFailedException extends BaseCustomException {
    public LoginFailedException() {
        super(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 잘못되었습니다.", 401);
    }
}
