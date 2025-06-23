package org.example.hugmeexp.global.infra.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RefreshRequest {

    @NotBlank
    @Size(min = 10)
    private String accessToken;

    @NotBlank
    @Size(min = 10)
    private String refreshToken;

    public RefreshRequest(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
