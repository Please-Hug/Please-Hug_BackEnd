package org.example.hugmeexp.domain.shop.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.shop.dto.ProductRequest;
import org.example.hugmeexp.domain.shop.entity.Product;
import org.example.hugmeexp.domain.shop.exception.ProductNotFoundException;
import org.example.hugmeexp.domain.shop.service.ProductAdminService;
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
    public ResponseEntity<Product> registerProduct(
            @ModelAttribute ProductRequest request) {
        try {
            Product registeredProduct = productAdminService.registerProduct(request);
            return ResponseEntity.ok(registeredProduct);
        } catch (IOException e) {
            log.warn("Error occurred while creating image file.");
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 상품 삭제
     * - 삭제할 상품의 Id를 입력받아 관련 ProductImage 엔티티 및 이미지 파일 삭제
     * @param productId
     * @return
     */
    @DeleteMapping("/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long productId) {
        try {
            productAdminService.deleteProduct(productId);
            return ResponseEntity.ok().body("Product successfully deleted");
        } catch (ProductNotFoundException e) {
            log.warn("Attempt to delete non-existent product: {}", productId);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 상품 수정
     * - form-data 형식으로 등록할 상품 정보 입력
     * - PathVariable로 수정할 상품 조회
     * - 관리자 권한 기능이어서 굳이 DTO로 감싸지 않아도 될 것 같아 Product 그대로 반환합니다.
     * @param productId
     * @param request
     * @return
     */
    @PutMapping("/{productId}")
    public ResponseEntity<Product> modifyProduct(
            @PathVariable Long productId,
            @ModelAttribute ProductRequest request) {
        try {
            Product modifiedProduct = productAdminService.modifyProduct(productId, request);
            return ResponseEntity.ok().body(modifiedProduct);
        } catch (ProductNotFoundException e) {
            log.warn("Attempt to modify non-existent product: {}", productId);
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            log.warn("Error occurred while creating image file.");
            return ResponseEntity.internalServerError().build();
        }
    }
}
