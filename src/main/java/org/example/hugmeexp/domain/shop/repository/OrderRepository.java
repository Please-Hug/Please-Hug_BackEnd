package org.example.hugmeexp.domain.shop.repository;

import org.example.hugmeexp.domain.shop.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
