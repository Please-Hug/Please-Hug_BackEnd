package org.example.hugmeexp.domain.shop.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class OutOfQuantityException extends BaseCustomException {
    public OutOfQuantityException() {
        super(HttpStatus.BAD_REQUEST, "The product is out of stock.");
    }
}
