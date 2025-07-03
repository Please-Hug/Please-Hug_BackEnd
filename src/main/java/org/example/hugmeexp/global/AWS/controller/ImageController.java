package org.example.hugmeexp.global.AWS.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.global.AWS.dto.ImageDeleteResponse;
import org.example.hugmeexp.global.AWS.dto.ImageUploadResponse;
import org.example.hugmeexp.global.AWS.dto.ImageUrlResponse;
import org.example.hugmeexp.global.AWS.service.ImageService;
import org.example.hugmeexp.global.common.response.Response;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
@Tag(name = "Image API", description = "이미지 업로드/삭제/조회 API")
public class ImageController {
    
    private final ImageService imageService;
    
    @SecurityRequirement(name = "JWT")
    @Operation(
        summary = "이미지 업로드 (범용)",
        description = "지정된 폴더에 이미지를 업로드합니다. " +
                     "지원 형식: JPEG, PNG, GIF, WebP (최대 10MB)"
    )
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Response<ImageUploadResponse>> uploadImage(
            @Parameter(description = "업로드할 이미지 파일", required = true)
            @RequestPart("image") MultipartFile image,
            
            @Parameter(description = "S3 폴더명 (예: study-diary-images, profile-images)", required = true)
            @RequestParam("folder") String folder,
            
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            String userId = userDetails.getUsername();
            ImageUploadResponse response = imageService.uploadImage(image, folder, userId);
            
            log.info("Image upload requested by user: {} to folder: {}", userId, folder);
            
            return ResponseEntity.ok(Response.<ImageUploadResponse>builder()
                    .message("이미지가 성공적으로 업로드되었습니다.")
                    .data(response)
                    .build());
                    
        } catch (IllegalArgumentException e) {
            log.warn("Invalid image upload request: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Response.<ImageUploadResponse>builder()
                            .message("업로드 실패: " + e.getMessage())
                            .build());
                            
        } catch (Exception e) {
            log.error("Image upload failed: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Response.<ImageUploadResponse>builder()
                            .message("이미지 업로드 중 서버 오류가 발생했습니다.")
                            .build());
        }
    }
    
    @SecurityRequirement(name = "JWT")
    @Operation(
        summary = "스터디 다이어리 이미지 업로드",
        description = "스터디 다이어리용 이미지를 업로드합니다. (study-diary-images 폴더)"
    )
    @PostMapping(value = "/studydiary", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Response<ImageUploadResponse>> uploadStudyDiaryImage(
            @Parameter(description = "업로드할 이미지 파일", required = true)
            @RequestPart("image") MultipartFile image,
            
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            String userId = userDetails.getUsername();
            ImageUploadResponse response = imageService.uploadStudyDiaryImage(image, userId);
            
            log.info("Study diary image upload requested by user: {}", userId);
            
            return ResponseEntity.ok(Response.<ImageUploadResponse>builder()
                    .message("스터디 다이어리 이미지가 성공적으로 업로드되었습니다.")
                    .data(response)
                    .build());
                    
        } catch (IllegalArgumentException e) {
            log.warn("Invalid study diary image upload request: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Response.<ImageUploadResponse>builder()
                            .message("업로드 실패: " + e.getMessage())
                            .build());
                            
        } catch (Exception e) {
            log.error("Study diary image upload failed: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Response.<ImageUploadResponse>builder()
                            .message("이미지 업로드 중 서버 오류가 발생했습니다.")
                            .build());
        }
    }
    
    @SecurityRequirement(name = "JWT")
    @Operation(
        summary = "프로필 이미지 업로드",
        description = "프로필용 이미지를 업로드합니다. (profile-images 폴더)"
    )
    @PostMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Response<ImageUploadResponse>> uploadProfileImage(
            @Parameter(description = "업로드할 이미지 파일", required = true)
            @RequestPart("image") MultipartFile image,
            
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            String userId = userDetails.getUsername();
            ImageUploadResponse response = imageService.uploadProfileImage(image, userId);
            
            log.info("Profile image upload requested by user: {}", userId);
            
            return ResponseEntity.ok(Response.<ImageUploadResponse>builder()
                    .message("프로필 이미지가 성공적으로 업로드되었습니다.")
                    .data(response)
                    .build());
                    
        } catch (IllegalArgumentException e) {
            log.warn("Invalid profile image upload request: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Response.<ImageUploadResponse>builder()
                            .message("업로드 실패: " + e.getMessage())
                            .build());
                            
        } catch (Exception e) {
            log.error("Profile image upload failed: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Response.<ImageUploadResponse>builder()
                            .message("이미지 업로드 중 서버 오류가 발생했습니다.")
                            .build());
        }
    }
    
    @SecurityRequirement(name = "JWT")
    @Operation(
        summary = "이미지 삭제",
        description = "S3에서 이미지를 삭제합니다. imageKey는 전체 경로를 포함해야 합니다."
    )
    @DeleteMapping("/{imageKey:.+}")
    public ResponseEntity<Response<ImageDeleteResponse>> deleteImage(
            @Parameter(description = "삭제할 이미지의 S3 키 (예: study-diary-images/user123_20241201_image.jpg)", required = true)
            @PathVariable String imageKey,
            
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            String userId = userDetails.getUsername();
            
            // 보안 검증: 자신이 업로드한 이미지만 삭제 가능
            if (!imageKey.contains(userId + "_")) {
                log.warn("Unauthorized image deletion attempt by user: {} for imageKey: {}", userId, imageKey);
                return ResponseEntity.badRequest()
                        .body(Response.<ImageDeleteResponse>builder()
                                .message("자신이 업로드한 이미지만 삭제할 수 있습니다.")
                                .build());
            }
            
            ImageDeleteResponse response = imageService.deleteImage(imageKey);
            
            log.info("Image deletion requested by user: {} for imageKey: {}", userId, imageKey);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(Response.<ImageDeleteResponse>builder()
                        .message("이미지가 성공적으로 삭제되었습니다.")
                        .data(response)
                        .build());
            } else {
                return ResponseEntity.internalServerError()
                        .body(Response.<ImageDeleteResponse>builder()
                                .message("이미지 삭제에 실패했습니다.")
                                .data(response)
                                .build());
            }
            
        } catch (Exception e) {
            log.error("Image deletion failed for imageKey: {} - {}", imageKey, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Response.<ImageDeleteResponse>builder()
                            .message("이미지 삭제 중 서버 오류가 발생했습니다.")
                            .build());
        }
    }
    
    @SecurityRequirement(name = "JWT")
    @Operation(
        summary = "이미지 URL 조회",
        description = "이미지의 Presigned URL을 생성합니다. (2분간 유효)"
    )
    @GetMapping("/{imageKey:.+}/url")
    public ResponseEntity<Response<ImageUrlResponse>> getImageUrl(
            @Parameter(description = "조회할 이미지의 S3 키", required = true)
            @PathVariable String imageKey,
            
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            ImageUrlResponse response = imageService.getImageUrl(imageKey);
            
            log.info("Image URL requested by user: {} for imageKey: {}", userDetails.getUsername(), imageKey);
            
            return ResponseEntity.ok(Response.<ImageUrlResponse>builder()
                    .message("이미지 URL이 성공적으로 생성되었습니다.")
                    .data(response)
                    .build());
                    
        } catch (Exception e) {
            log.error("Image URL generation failed for imageKey: {} - {}", imageKey, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Response.<ImageUrlResponse>builder()
                            .message("이미지 URL 생성 중 오류가 발생했습니다.")
                            .build());
        }
    }
} 