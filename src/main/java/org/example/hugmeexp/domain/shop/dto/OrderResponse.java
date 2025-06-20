package org.example.hugmeexp.domain.shop.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private String imageUrl;
    private String brand;
    private String name;
    private String price;
    private LocalDateTime orderTime;
    private String receiverPhoneNumber;
}
