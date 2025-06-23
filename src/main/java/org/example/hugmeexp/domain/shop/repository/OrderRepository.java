package org.example.hugmeexp.domain.shop.repository;

import org.example.hugmeexp.domain.shop.entity.Order;
import org.example.hugmeexp.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByUserAndCreatedAtBetween(User user, LocalDateTime start, LocalDateTime end);
}
