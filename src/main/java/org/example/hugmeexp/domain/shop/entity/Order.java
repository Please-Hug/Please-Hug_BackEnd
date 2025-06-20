package org.example.hugmeexp.domain.shop.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hugmeexp.global.entity.BaseEntity;
import org.example.hugmeexp.domain.user.entity.User;

@Getter
@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Order와 User는 N:1 관계이고 Order -> User의 단방향 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Order와 Product는 N:1 관계이고 Order -> Product의 단방향 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, length = 13)
    private String phoneNumber;

    @Builder
    private Order(User user, Product product, String phoneNumber) {
        this.user = user;
        this.product = product;
        this.phoneNumber = phoneNumber;
    }

    // 정적 팩토리 메서드
    public static Order createOrder(User user, Product product, String phoneNumber) {
        return Order.builder()
                .user(user)
                .product(product)
                .phoneNumber(phoneNumber)
                .build();
    }
}
