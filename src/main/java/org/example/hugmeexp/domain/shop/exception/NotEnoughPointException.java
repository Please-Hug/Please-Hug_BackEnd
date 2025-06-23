package org.example.hugmeexp.domain.shop.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class NotEnoughPointException extends BaseCustomException {
    public NotEnoughPointException() {
        super(HttpStatus.BAD_REQUEST, "Not enough point to purchase product.");
    }
}
