package org.example.hugmeexp.domain.attendance.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class UsernameTooLongException extends BaseCustomException {
    private static final String MESSAGE = "username must be under 32 characters long";
    private static final int CODE = 400;

    public UsernameTooLongException() { super(HttpStatus.BAD_REQUEST, MESSAGE, CODE);}
}


