package org.example.hugmeexp.global.infra.auth.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class TokenReuseDetectedException extends BaseCustomException {
    public TokenReuseDetectedException() {
        super(HttpStatus.UNAUTHORIZED, "재사용된 리프레시 토큰입니다. 보안 위협이 감지되었습니다.", 401);
    }
}
