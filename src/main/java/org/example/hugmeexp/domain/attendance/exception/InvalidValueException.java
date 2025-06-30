package org.example.hugmeexp.domain.attendance.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class InvalidValueException extends BaseCustomException {

    public InvalidValueException(String message) {
        super(HttpStatus.BAD_REQUEST, message, 400);
    }
}
