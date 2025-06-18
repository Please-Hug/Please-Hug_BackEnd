package org.example.hugmeexp.domain.shop.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Entity
@Table(name = "product_image")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 36)
    private String uuid;

    @Column(nullable = false)
    private String path;

    @Column(nullable = false)
    private String extension;

    @Builder
    private ProductImage(String path, String extension) {
        this.uuid = UUID.randomUUID().toString();
        this.path = path;
        this.extension = extension;
    }

    public static ProductImage registerProductImage(String path, String extension) {
        return ProductImage.builder()
                .path(path)
                .extension(extension)
                .build();
    }
}
