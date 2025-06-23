package org.example.hugmeexp.global.infra.auth.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class AccessTokenStillValidException extends BaseCustomException {
    public AccessTokenStillValidException() {
        super(HttpStatus.BAD_REQUEST, "현재 액세스 토큰이 유효하므로 재발급이 허용되지 않습니다.", 400);
    }
}
