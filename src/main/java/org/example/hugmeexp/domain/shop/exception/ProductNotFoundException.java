package org.example.hugmeexp.domain.shop.exception;

// 입력받은 Id에 대해 Product 엔티티를 찾지 못한 경우 예외 처리
public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String message) {
        super(message);
    }
}
