package org.example.hugmeexp.domain.shop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.shop.dto.ProductRequest;
import org.example.hugmeexp.domain.shop.dto.ProductResponse;
import org.example.hugmeexp.domain.shop.entity.Product;
import org.example.hugmeexp.domain.shop.service.ProductAdminService;
import org.example.hugmeexp.global.common.response.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/shop")
@Slf4j
@Tag(name = "Admin - Product", description = "상점 관련 관리자 API")
public class ProductAdminController {

    private final ProductAdminService productAdminService;

    @PostMapping
    @Operation(summary = "상품 등록", description = "관리자가 상품을 등록한다.")
    public ResponseEntity<Response<?>> registerProduct(
            @ModelAttribute ProductRequest request) {
        Product registeredProduct = productAdminService.registerProduct(request);
        return ResponseEntity.status(201).body(Response.builder().data(registeredProduct).message("Product is successfully registered").build());
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "상품 삭제", description = "관리자가 상품을 삭제한다.")
    public ResponseEntity<Response<?>> deleteProduct(@PathVariable Long productId) {
        productAdminService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{productId}")
    @Operation(summary = "상품 수정", description = "관리자가 상품을 수정한다.")
    public ResponseEntity<Response<?>> modifyProduct(
            @PathVariable Long productId,
            @ModelAttribute ProductRequest request) {
        ProductResponse modifiedProduct = productAdminService.modifyProduct(productId, request);
        return ResponseEntity.ok().body(Response.builder().data(modifiedProduct).message("Product with ID " + productId + " modified").build());
    }
}
