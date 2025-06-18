package org.example.hugmeexp.domain.shop.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hugmeexp.domain.shop.dto.ProductRequest;

@Getter
@Entity
@Table(name = "product")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Product와 ProductImage는 1:1 관계이고 Product -> ProductImage의 단방향 매핑
    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "product_image_id", nullable = true)
    private ProductImage productImage;

    // 상품명
    @Column(nullable = false)
    private String name;

    // 브랜드
    @Column(nullable = false)
    private String brand;

    // 재고
    @Column(nullable = false)
    private Integer quantity;

    // 가격
    @Column(nullable = false)
    private Integer price;

    @Builder
    private Product(String name, String brand, Integer quantity, Integer price) {
        this.name = name;
        this.brand = brand;
        this.quantity = quantity;
        this.price = price;
    }

    // 정적 팩토리 메서드
    public static Product createProduct(String name, String brand, Integer quantity, Integer price) {
        return Product.builder()
                .name(name)
                .brand(brand)
                .quantity(quantity)
                .price(price)
                .build();
    }

    /*
        이미지 변경 요청 시 (isRegisterProductImage 호출)
        1. 이미지가 있는 경우
        - 서비스 계층에서 기존 이미지 삭제 및 ProductImage 삭제
        - registerProductImage 호출
        2. 이미지가 없는 경우
        - registerProductImage 호출
     */
    public ProductImage registerProductImage(String path, String extension) {
        this.productImage = ProductImage.registerProductImage(path, extension);
        return this.productImage;
    }

    public boolean isRegisterProductImage() {
        return this.productImage != null;
    }

    public void updateProduct(ProductRequest dto) {

        this.name = dto.getName();
        this.brand = dto.getBrand();
        this.quantity = dto.getQuantity();
        this.price = dto.getPrice();
    }
}
