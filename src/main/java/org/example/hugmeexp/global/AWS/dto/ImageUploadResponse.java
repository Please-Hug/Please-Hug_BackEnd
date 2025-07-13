package org.example.hugmeexp.global.AWS.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "이미지 업로드 응답")
public class ImageUploadResponse {
    
    @Schema(description = "업로드된 이미지의 S3 키", example = "study-diary-images/user123_20241201_image.jpg")
    private String imageKey;
    
    @Schema(description = "이미지 접근 URL", example = "https://bucket.s3.region.amazonaws.com/study-diary-images/user123_20241201_image.jpg")
    private String imageUrl;
    
    @Schema(description = "원본 파일명", example = "my-photo.jpg")
    private String originalFileName;
    
    @Schema(description = "저장된 파일명", example = "user123_20241201_image.jpg")
    private String storedFileName;
    
    @Schema(description = "파일 크기 (bytes)", example = "1048576")
    private Long fileSize;
    
    @Schema(description = "MIME 타입", example = "image/jpeg")
    private String contentType;
    
    @Schema(description = "Markdown 형식 이미지 링크", example = "![my-photo.jpg](https://bucket.s3.region.amazonaws.com/study-diary-images/user123_20241201_image.jpg)")
    private String markdownImageLink;
} 