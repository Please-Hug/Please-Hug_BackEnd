package org.example.hugmeexp.domain.shop.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.shop.dto.ProductRequest;
import org.example.hugmeexp.domain.shop.dto.ProductResponse;
import org.example.hugmeexp.domain.shop.entity.Product;
import org.example.hugmeexp.domain.shop.entity.ProductImage;
import org.example.hugmeexp.domain.shop.exception.ProductDeletedException;
import org.example.hugmeexp.domain.shop.exception.ProductNotFoundException;
import org.example.hugmeexp.domain.shop.mapper.ProductMapper;
import org.example.hugmeexp.domain.shop.repository.ProductImageRepository;
import org.example.hugmeexp.domain.shop.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductAdminService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductMapper productMapper;

    /**
     * 상품 등록 메서드
     * @param request
     * @return 생성된 Product의 Id
     * @throws IOException
     */
    @Transactional
    public Product registerProduct(ProductRequest request) {

        // Product 객체 생성
        Product product = Product.createProduct(
                request.getName(),
                request.getBrand(),
                request.getQuantity(),
                request.getPrice()
        );

        // 이미지 정보가 있는 경우 이미지 등록
        MultipartFile image = request.getImage();
        if (image != null && !image.isEmpty()) {

            // Product에 대한 ProductImage 생성 후 연결
            createImage(product, image);
        }

        Product savedProduct = productRepository.save(product);
        log.info("Product saved with ID: {}", savedProduct.getId());
        return product;
    }

    /**
     * 상품 삭제 메서드
     * @param productId
     */
    @Transactional
    public void deleteProduct(Long productId) {

        // Id와 일치하는 상품이 없다면 예외 처리
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        // 해당 상품이 이미 삭제된 상품이면 예외 처리
        if (product.isDeleted()) {
            throw new ProductDeletedException();
        }

        log.info("Attempting to delete product ID: {}", productId);

        // 상품 삭제는 논리 삭제로 구현 (삭제된 상품이 과거 주문과 연관될 수 있음)
        product.delete();
        productRepository.save(product);

        log.info("Product successfully deleted.");
    }

    /**
     * 상품 수정 메서드
     * @param productId
     * @param request
     * @return
     */
    @Transactional
    public ProductResponse modifyProduct(Long productId, ProductRequest request) {

        // Id와 일치하는 상품이 없다면 예외 처리
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        // 해당 상품이 이미 삭제된 상품이면 예외 처리
        if (product.isDeleted()) {
            throw new ProductDeletedException();
        }

        // product 조회
        log.info("Modify product ID: {}", productId);

        // product 엔티티 수정 및 이미지 존재할 시 기존 이미지 삭제 후 생성
        product.updateProduct(request);

        MultipartFile image = request.getImage();
        if (image != null && !image.isEmpty()) {

            // product 엔티티에 이미지가 있었으면 기존 ProductImage 삭제 후 재생성
            if (product.isRegisterProductImage()) {
                deleteImage(product);
            }
            createImage(product, image);
        }

        Product savedProduct = productRepository.save(product);
        log.info("Product saved with ID: {}", savedProduct.getId());
        ProductResponse response = productMapper.toResponse(savedProduct);
        return response;
    }


    // ===== private methods =====
    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    private void createImage(Product product, MultipartFile image) {

        // 확장자 추출
        String extension = getExtension(image.getOriginalFilename());

        // 파일 저장 경로 (임시 디렉터리 '\AppData\Local\Temp\app-uploads\', 없다면 생성)
        Path uploadDirPath = Paths.get(System.getProperty("java.io.tmpdir"), "app-uploads");

        // 확장자와 저장 경로를 통해 ProductImage 엔티티 생성 및 Product 엔티티와 매핑
        try {

            // 파일을 저장할 디렉터리가 없으면 생성
            if (!Files.exists(uploadDirPath)) {
                Files.createDirectories(uploadDirPath);
            }

            // 확장자와 저장 경로를 통해 ProductImage 생성
            // 해당 ProductImage를 Product와 연결
            ProductImage productImage = product.registerProductImage(uploadDirPath.toString(), extension);

            // \AppData\Local\Temp\app-uploads\ 경로에 uuid.extension 형식으로 파일 저장
            String savedFileName = productImage.getUuid() + "." + productImage.getExtension();
            Path savePath = uploadDirPath.resolve(savedFileName);
            image.transferTo(savePath.toFile());

            // 파일 저장 성공 여부에 따른 로그 출력
            if (Files.exists(savePath)) {
                log.info("File saved successfully: {}", savePath);
            } else {
                log.error("File was not saved: {}", savePath);
            }

        } catch (IOException e) {
            log.error("Error occurred while saving file", e);
        }
    }

    private void deleteImage(Product product) {

        // product와 연관된 ProductImage 및 이미지 파일 삭제
        try {
            ProductImage productImage = product.getProductImage();
            Path imagePath = Paths.get(productImage.getPath(),
                    productImage.getUuid() + "." + productImage.getExtension());
            Files.deleteIfExists(imagePath);
            log.info("Deleted image file: {}", imagePath);

            // 이미지 삭제 후 ProductImage 엔티티 삭제
            productImageRepository.delete(productImage);
        } catch (IOException e) {
            log.error("Failed to delete image file for product ID: {}", product.getId(), e);
        }
    }
}
