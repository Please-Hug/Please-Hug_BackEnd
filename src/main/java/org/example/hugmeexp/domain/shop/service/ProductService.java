package org.example.hugmeexp.domain.shop.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.shop.dto.ProductResponse;
import org.example.hugmeexp.domain.shop.dto.PurchaseRequest;
import org.example.hugmeexp.domain.shop.dto.PurchaseResponse;
import org.example.hugmeexp.domain.shop.entity.Order;
import org.example.hugmeexp.domain.shop.entity.Product;
import org.example.hugmeexp.domain.shop.exception.*;
import org.example.hugmeexp.domain.shop.mapper.ProductMapper;
import org.example.hugmeexp.domain.shop.repository.OrderRepository;
import org.example.hugmeexp.domain.shop.repository.ProductRepository;
import org.example.hugmeexp.domain.user.repository.UserRepository;
import org.example.hugmeexp.domain.user.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductMapper productMapper;

    /**
     * 전체 상품 조회 메서드
     * - 삭제된 상품은 조회되지 않음
     * @return
     */
    public List<ProductResponse> getAllProducts() {

        log.info("전체 상품 조회 요청");
        return productRepository.findAllByIsDeletedFalse().stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 상품 구매 메서드
     * @param purchaserUsername
     * @param request
     * @return
     */
    @Transactional
    public PurchaseResponse purchase(String purchaserUsername, PurchaseRequest request) {

        // 구매자 정보가 없다면 예외 처리
        User purchaser = userRepository.findByUsername(purchaserUsername)
                .orElseThrow(() -> new UserNotFoundInPurchaseException());

        // Id와 일치하는 상품이 없다면 예외 처리
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(request.getProductId()));

        // 해당 상품이 이미 삭제된 상품이면 예외 처리
        if (product.isDeleted()) {
            throw new ProductDeletedException();
        }

        log.info("Attempting to Purchase product ID: {}", product.getId());

        // 유저의 포인트보다 상품 가격이 높으면 포인트 부족 예외 처리
        if (purchaser.getPoint() < product.getPrice()) {
            log.info("User's point:{} 는 상품의 가격:{} 보다 낮습니다.", purchaser.getPoint(), product.getPrice());
            throw new NotEnoughPointException();
        }

        // 상품의 재고가 0이면 재고 부족 예외 처리
        if (product.getQuantity() == 0) {
            log.info("상품 재고가 부족합니다.");
            throw new OutOfQuantityException();
        }

        // 수령자 정보가 없다면 예외 처리
        User receiver = userRepository.findByUsername(request.getReceiverUsername())
                .orElseThrow(() -> new UserNotFoundInPurchaseException());


        // Order 객체 생성
        Order order = Order.createOrder(
                purchaser,
                product,
                receiver.getPhoneNumber()
        );

        purchaser.increasePoint(product.getPrice() * (-1));
        product.decreaseQuantity();

        orderRepository.save(order);
        userRepository.save(purchaser);
        productRepository.save(product);

        PurchaseResponse response = PurchaseResponse.builder()
                .purchaserName(purchaser.getName())
                .remainingPoint(purchaser.getPoint())
                .productName(product.getName())
                .productQuantity(product.getQuantity())
                .phoneNumber(order.getPhoneNumber())
                .purchaseTime(order.getCreatedAt())
                .build();
        return response;
    }


    // ===== 테스트용 =====
    public void increasePoint(String username) {
        User user = userRepository.findByUsername(username).get();
        user.increasePoint(100000);
        userRepository.save(user);
    }
}
