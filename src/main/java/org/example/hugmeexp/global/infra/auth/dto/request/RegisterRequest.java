package org.example.hugmeexp.global.infra.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RegisterRequest {

    @Schema(description = "회원가입 시 사용할 사용자 ID (4~32자)", example = "seungwook0924")
    @NotBlank
    @Size(min = 4, max = 32)
    private String username;

    @Schema(description = "비밀번호 (8~60자", example = "strongPassword123!")
    @NotBlank
    @Size(min = 8, max = 60)
    private String password;

    @Schema(description = "사용자 이름 (2~32자)", example = "홍길동")
    @NotBlank
    @Size(min = 2, max = 32)
    private String name;

    @Schema(description = "전화번호 (하이픈 포함 형식만 허용)", example = "010-1234-5678", pattern = "^01[0-9]-\\d{4}-\\d{4}$")
    @NotBlank
    @Pattern(regexp = "^01[0-9]-\\d{4}-\\d{4}$")
    private String phoneNumber;

    public RegisterRequest(String username, String password, String name, String phoneNumber) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }
}