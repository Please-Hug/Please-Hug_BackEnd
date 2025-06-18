package org.example.hugmeexp.domain.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    // nullable
    private MultipartFile image;

    private String name;
    private String brand;
    private Integer quantity;
    private Integer price;
}
