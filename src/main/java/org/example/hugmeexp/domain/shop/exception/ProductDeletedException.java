package org.example.hugmeexp.domain.shop.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class ProductDeletedException extends BaseCustomException {
    public ProductDeletedException() {
        super(HttpStatus.BAD_REQUEST, "Already deleted product.");
    }
}
