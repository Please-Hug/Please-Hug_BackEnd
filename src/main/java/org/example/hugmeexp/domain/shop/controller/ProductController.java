package org.example.hugmeexp.domain.shop.controller;

import lombok.RequiredArgsConstructor;
import org.example.hugmeexp.domain.shop.dto.ProductResponse;
import org.example.hugmeexp.domain.shop.service.ProductService;
import org.example.hugmeexp.global.common.response.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/shop")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * 전체 상품 조회
     * @return
     */
    @GetMapping
    public ResponseEntity<Response<?>> getAllProducts() {
        List<ProductResponse> allProduct = productService.getAllProducts();
        return ResponseEntity.ok().body(Response.builder().data(allProduct).message("Successfully retrieved all products.").build());
    }
}
