package org.example.hugmeexp.global.AWS.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "이미지 URL 조회 응답")
public class ImageUrlResponse {
    
    @Schema(description = "이미지 S3 키", example = "study-diary-images/user123_20241201_image.jpg")
    private String imageKey;
    
    @Schema(description = "Presigned URL", example = "https://bucket.s3.region.amazonaws.com/study-diary-images/user123_20241201_image.jpg?AWSAccessKeyId=...")
    private String presignedUrl;
    
    @Schema(description = "URL 만료 시간 (분)", example = "2")
    private int expirationMinutes;
    
    @Schema(description = "URL 생성 시간", example = "2024-12-01T10:30:00")
    private String generatedAt;
} 