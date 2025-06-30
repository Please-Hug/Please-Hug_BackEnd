package org.example.hugmeexp.domain.shop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "User - Product", description = "상점 관련 사용자 API")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "전체 상품 조회", description = "모든 상품 목록을 조회한다.")
    public ResponseEntity<Response<?>> getAllProducts(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();
        log.info("login user: {}", user.getUsername());
        List<ProductResponse> allProduct = productService.getAllProducts(user);
        return ResponseEntity.ok().body(Response.builder().data(allProduct).message("Successfully retrieved all products.").build());
    }

    @PostMapping("/purchase")
    @Operation(summary = "상품 구매", description = "수령인의 아이디를 입력해 상품을 구매한다.")
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

    @GetMapping("/history")
    @Operation(summary = "주문 내역 조회", description = "상품 구매 현황을 조회한다.")
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
}
