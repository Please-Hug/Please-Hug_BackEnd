package org.example.hugmeexp.domain.shop.repository;

import org.example.hugmeexp.domain.shop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
