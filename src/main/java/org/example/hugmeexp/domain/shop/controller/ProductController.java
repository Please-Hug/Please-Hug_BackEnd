package org.example.hugmeexp.domain.shop.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.shop.dto.OrderResponse;
import org.example.hugmeexp.domain.shop.dto.ProductResponse;
import org.example.hugmeexp.domain.shop.dto.PurchaseRequest;
import org.example.hugmeexp.domain.shop.dto.PurchaseResponse;
import org.example.hugmeexp.domain.shop.service.ProductService;
import org.example.hugmeexp.domain.user.entity.User;
import org.example.hugmeexp.global.common.response.Response;
import org.example.hugmeexp.global.security.CustomUserDetails;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/shop")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    /**
     * 전체 상품 조회
     * @return
     */
    @GetMapping
    public ResponseEntity<Response<?>> getAllProducts(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();
        log.info("login user: {}", user.getUsername());
        List<ProductResponse> allProduct = productService.getAllProducts(user);
        return ResponseEntity.ok().body(Response.builder().data(allProduct).message("Successfully retrieved all products.").build());
    }

    /**
     * 상품 구매
     * - 로그인된 사용자 정보와 구매할 상품 ID, 수령자 username 데이터로 요청
     * - 구매 완료된 주문에 대한 구매자, 상품, 수령자 번호 정보를 응답
     * @param request
     * @param userDetails
     * @return
     */
    @PostMapping("/purchase")
    public ResponseEntity<Response<?>> purchaseProduct(
            @RequestBody PurchaseRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        log.info("purchaser's username: {}", userDetails.getUsername());
        log.info("product ID: {}", request.getProductId());
        log.info("receiver's username: {}", request.getReceiverUsername());
        String purchaserUsername = userDetails.getUsername();
        PurchaseResponse response = productService.purchase(purchaserUsername, request);
        return ResponseEntity.ok().body(Response.builder().data(response).message("Successfully purchased product.").build());
    }

    /**
     * 주문 내역 조회
     * @param startDate
     * @param endDate
     * @param userDetails
     * @return
     */
    @GetMapping("/history")
    public ResponseEntity<Response<?>> getOrders(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (endDate == null) {
            endDate = LocalDate.now();
        }
        if (startDate == null) {
            startDate = endDate.minusMonths(1);
        }
        User user = userDetails.getUser();

        log.info("start date: {}", startDate);
        log.info("end date: {}", endDate);

        List<OrderResponse> response = productService.getOrders(user, startDate, endDate);
        return ResponseEntity.ok().body(Response.builder().data(response).message("Successfully retrieved orders.").build());
    }

//    // ===== 사용자 구매 테스트를 위한 포인트 증가 메서드 =====
//    @PostMapping("/point")
//    public void increasePoint(
//            @AuthenticationPrincipal CustomUserDetails userDetails) {
//        productService.increasePoint(userDetails.getUsername());
//    }
}
