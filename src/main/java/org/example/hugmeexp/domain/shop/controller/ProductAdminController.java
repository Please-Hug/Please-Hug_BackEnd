package org.example.hugmeexp.domain.shop.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.shop.dto.ProductRequest;
import org.example.hugmeexp.domain.shop.dto.ProductResponse;
import org.example.hugmeexp.domain.shop.entity.Product;
import org.example.hugmeexp.domain.shop.service.ProductAdminService;
import org.example.hugmeexp.global.common.response.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/shop")
@Slf4j
public class ProductAdminController {

    private final ProductAdminService productAdminService;

    /**
     * 상품 등록
     * - form-data 형식으로 등록할 상품 정보 입력
     * - 이미지 파일은 생략 가능
     * @param request
     * @return
     * @throws IOException
     */
    @PostMapping
    public ResponseEntity<Response<?>> registerProduct(
            @ModelAttribute ProductRequest request) {
        Product registeredProduct = productAdminService.registerProduct(request);
        return ResponseEntity.status(201).body(Response.builder().data(registeredProduct).message("Product is successfully registered").build());
    }

    /**
     * 상품 삭제
     * - 삭제할 상품의 Id를 입력받아 관련 ProductImage 엔티티 및 이미지 파일 삭제
     * @param productId
     * @return
     */
    @DeleteMapping("/{productId}")
    public ResponseEntity<Response<?>> deleteProduct(@PathVariable Long productId) {
        productAdminService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 상품 수정
     * - form-data 형식으로 등록할 상품 정보 입력
     * - PathVariable로 수정할 상품 조회
     * @param productId
     * @param request
     * @return
     */
    @PutMapping("/{productId}")
    public ResponseEntity<Response<?>> modifyProduct(
            @PathVariable Long productId,
            @ModelAttribute ProductRequest request) {
        ProductResponse modifiedProduct = productAdminService.modifyProduct(productId, request);
        return ResponseEntity.ok().body(Response.builder().data(modifiedProduct).message("Product with ID " + productId + " modified").build());
    }
}
