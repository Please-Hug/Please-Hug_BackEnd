package org.example.hugmeexp.domain.shop.dto;


import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseResponse {

    // 주문자 정보
    private String purchaserName;
    private int remainingPoint;

    // 상품 정보
    private String productName;
    private int productQuantity;

    // 수령자 번호
    private String phoneNumber;

    // 구매 시각
    private LocalDateTime purchaseTime;
}