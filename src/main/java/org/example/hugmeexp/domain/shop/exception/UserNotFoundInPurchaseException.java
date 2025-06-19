package org.example.hugmeexp.domain.shop.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class UserNotFoundInPurchaseException extends BaseCustomException {
    public UserNotFoundInPurchaseException() {
        super(HttpStatus.NOT_FOUND, "There is no user.");
    }
}
