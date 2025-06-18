package org.example.hugmeexp.global.infra.auth.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class InvalidRefreshTokenException extends BaseCustomException {
    public InvalidRefreshTokenException() {
        super(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다.", 401);
    }
}
