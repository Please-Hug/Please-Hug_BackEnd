package org.example.hugmeexp.global.infra.auth.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class InvalidAccessTokenException extends BaseCustomException {
    public InvalidAccessTokenException() {
        super(HttpStatus.UNAUTHORIZED, "유효하지 않은 액세스 토큰입니다.", 401);
    }
}
