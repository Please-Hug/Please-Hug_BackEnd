package org.example.hugmeexp.global.infra.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RefreshRequest {

    @Schema(description = "만료된 액세스 토큰 (Bearer 없이 순수 JWT 문자열)", example = "eyJhbGciOiJIU...")
    @NotBlank
    @Size(min = 10)
    private String accessToken;

    @Schema(description = "유효한 리프레시 토큰", example = "dGhpc2lzYXJl...")
    @NotBlank
    @Size(min = 10)
    private String refreshToken;

    public RefreshRequest(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
