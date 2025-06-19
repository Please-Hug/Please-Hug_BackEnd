package org.example.hugmeexp.domain.shop.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class ProductNotFoundException extends BaseCustomException {
    public ProductNotFoundException(Long productId) {
        super(HttpStatus.NOT_FOUND, "There is no product with ID: " + productId);
    }
}
