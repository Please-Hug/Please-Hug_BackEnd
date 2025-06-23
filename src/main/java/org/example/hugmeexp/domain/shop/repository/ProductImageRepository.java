package org.example.hugmeexp.domain.shop.repository;

import org.example.hugmeexp.domain.shop.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
}
