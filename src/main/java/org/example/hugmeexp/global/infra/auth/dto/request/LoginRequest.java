package org.example.hugmeexp.global.infra.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequest {

    @Schema(description = "사용자 ID (4자 이상 32자 이하)", example = "testuser123")
    @NotBlank
    @Size(min = 4, max = 32)
    private String username;

    @Schema(description = "비밀번호 (8자 이상 60자 이하)", example = "P@ssw0rd!")
    @NotBlank
    @Size(min = 8, max = 60)
    private String password;

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}