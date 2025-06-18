package org.example.hugmeexp.global.infra.auth.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class PhoneNumberDuplicatedException extends BaseCustomException {
    public PhoneNumberDuplicatedException() {
        super(HttpStatus.CONFLICT, "이미 존재하는 휴대폰 번호입니다.", 409);
    }
}
