package org.example.hugmeexp.global.AWS.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.global.AWS.dto.ImageDeleteResponse;
import org.example.hugmeexp.global.AWS.dto.ImageUploadResponse;
import org.example.hugmeexp.global.AWS.dto.ImageUrlResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class ImageService {
    
    private final S3Service s3Service;
    
    // 허용되는 이미지 타입
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
        "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );
    
    // 최대 파일 크기 (10MB)
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    
    /**
     * 이미지 업로드
     * @param file 업로드할 이미지 파일
     * @param folder S3 내 폴더명 (예: "study-diary-images", "profile-images")
     * @param userId 사용자 ID (파일명에 사용)
     * @return ImageUploadResponse
     */
    public ImageUploadResponse uploadImage(MultipartFile file, String folder, String userId) {
        try {
            // 파일 검증
            validateImageFile(file);
            
            // 파일명 생성
            String storedFileName = generateFileName(file.getOriginalFilename(), userId);
            String imageKey = folder + "/" + storedFileName;
            
            // S3 업로드
            String imageUrl = s3Service.upload(file, imageKey);
            
            log.info("Image uploaded successfully: {} -> {}", file.getOriginalFilename(), imageKey);
            
            // 마크다운 이미지 링크 생성
            String markdownImageLink = String.format("![%s](%s)", 
                file.getOriginalFilename(), imageUrl);
            
            return ImageUploadResponse.builder()
                    .imageKey(imageKey)
                    .imageUrl(imageUrl)
                    .originalFileName(file.getOriginalFilename())
                    .storedFileName(storedFileName)
                    .fileSize(file.getSize())
                    .contentType(file.getContentType())
                    .markdownImageLink(markdownImageLink)
                    .build();
                    
        } catch (IOException e) {
            log.error("Failed to upload image: {}", e.getMessage());
            throw new RuntimeException("이미지 업로드 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 스터디 다이어리 이미지 업로드 (편의 메소드)
     */
    public ImageUploadResponse uploadStudyDiaryImage(MultipartFile file, String userId) {
        return uploadImage(file, "study-diary-images", userId);
    }
    
    /**
     * 프로필 이미지 업로드 (편의 메소드)
     */
    public ImageUploadResponse uploadProfileImage(MultipartFile file, String userId) {
        return uploadImage(file, "profile-images", userId);
    }
    
    /**
     * 이미지 삭제
     * @param imageKey 삭제할 이미지의 S3 키
     * @return ImageDeleteResponse
     */
    public ImageDeleteResponse deleteImage(String imageKey) {
        try {
            s3Service.delete(imageKey);
            
            log.info("Image deleted successfully: {}", imageKey);
            
            return ImageDeleteResponse.builder()
                    .deletedImageKey(imageKey)
                    .success(true)
                    .deletedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .build();
                    
        } catch (Exception e) {
            log.error("Failed to delete image: {} - {}", imageKey, e.getMessage());
            
            return ImageDeleteResponse.builder()
                    .deletedImageKey(imageKey)
                    .success(false)
                    .deletedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .build();
        }
    }
    
    /**
     * 이미지 Presigned URL 조회
     * @param imageKey 조회할 이미지의 S3 키
     * @return ImageUrlResponse
     */
    public ImageUrlResponse getImageUrl(String imageKey) {
        try {
            String presignedUrl = s3Service.getPresignedURL(imageKey);
            
            return ImageUrlResponse.builder()
                    .imageKey(imageKey)
                    .presignedUrl(presignedUrl)
                    .expirationMinutes(2)
                    .generatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .build();
                    
        } catch (Exception e) {
            log.error("Failed to generate presigned URL for image: {} - {}", imageKey, e.getMessage());
            throw new RuntimeException("이미지 URL 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 이미지 파일 검증
     */
    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("이미지 파일이 비어있습니다.");
        }
        
        // 파일 크기 검증
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("파일 크기가 너무 큽니다. 최대 10MB까지 업로드 가능합니다.");
        }
        
        // 파일 타입 검증
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("지원하지 않는 이미지 형식입니다. (지원 형식: JPEG, PNG, GIF, WebP)");
        }
        
        // 파일명 검증
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new IllegalArgumentException("파일명이 유효하지 않습니다.");
        }
        
        // 확장자 검증
        if (!originalFilename.contains(".")) {
            throw new IllegalArgumentException("파일 확장자가 없습니다.");
        }
    }
    
    /**
     * 고유한 파일명 생성
     * 형식: {userId}_{timestamp}_{uuid}_{originalName}
     */
    private String generateFileName(String originalFilename, String userId) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        
        // 원본 파일명에서 확장자 추출
        String extension = "";
        int lastDotIndex = originalFilename.lastIndexOf(".");
        if (lastDotIndex > 0) {
            extension = originalFilename.substring(lastDotIndex);
            originalFilename = originalFilename.substring(0, lastDotIndex);
        }
        
        // 파일명 길이 제한 (S3 키 길이 제한 고려)
        if (originalFilename.length() > 30) {
            originalFilename = originalFilename.substring(0, 30);
        }
        
        // 특수문자 제거 및 안전한 파일명 생성
        String safeName = originalFilename.replaceAll("[^a-zA-Z0-9가-힣._-]", "_");
        
        return String.format("%s_%s_%s_%s%s", userId, timestamp, uuid, safeName, extension);
    }
} 