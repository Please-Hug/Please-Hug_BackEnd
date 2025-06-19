package org.example.hugmeexp.global.infra.auth.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class InvalidPositiveValueException extends BaseCustomException {
    public InvalidPositiveValueException() {
        super(HttpStatus.BAD_REQUEST, "유효하지 않은 값이 입력되었습니다. 양수만 허용됩니다.", HttpStatus.BAD_REQUEST.value());
    }
}
