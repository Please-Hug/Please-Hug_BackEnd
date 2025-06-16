package org.example.hugmeexp.global.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BaseCustomException extends RuntimeException { //RuntimeException 상속 안하면 Spring에서 인식 못함
    private final HttpStatus httpStatus;
    private final int code;
    private final String message;

    public BaseCustomException(HttpStatus httpStatus, String message, int code) {
        this.httpStatus = httpStatus;
        this.message = message;
        this.code = code;
    }
}
