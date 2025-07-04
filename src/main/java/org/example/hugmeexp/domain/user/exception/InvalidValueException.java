package org.example.hugmeexp.domain.user.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class InvalidValueException extends BaseCustomException {
    public InvalidValueException(String message) {
        super(HttpStatus.BAD_REQUEST, message, HttpStatus.BAD_REQUEST.value());
    }
}
