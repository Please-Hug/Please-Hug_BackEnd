package org.example.hugmeexp.domain.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private Long id;
    private String name;
    private String brand;
    private int quantity;
    private int price;
    private String imageUrl;

    // 로그인 사용자가 구매 가능한 상품인지
    private boolean available = false;
}
