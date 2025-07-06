package org.example.hugmeexp.global.AWS.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "이미지 삭제 응답")
public class ImageDeleteResponse {
    
    @Schema(description = "삭제된 이미지의 S3 키", example = "study-diary-images/user123_20241201_image.jpg")
    private String deletedImageKey;
    
    @Schema(description = "삭제 성공 여부", example = "true")
    private boolean success;
    
    @Schema(description = "삭제 시간", example = "2024-12-01T10:30:00")
    private String deletedAt;
} 