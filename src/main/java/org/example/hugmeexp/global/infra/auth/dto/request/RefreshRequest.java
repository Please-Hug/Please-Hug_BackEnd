package org.example.hugmeexp.global.infra.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class RefreshRequest {
    @NotBlank
    @Size(min = 10)
    private String accessToken;

    @NotBlank
    @Size(min = 10)
    private String refreshToken;
}
