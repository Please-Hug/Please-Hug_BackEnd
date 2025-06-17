package org.example.hugmeexp.global.infra.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegisterRequest {
    @NotBlank
    @Size(min = 4, max = 32, message = "아이디가 너무 길거나 짧습니다.")
    private String username;

    @NotBlank
    @Size(min = 8, max = 60, message = "비밀번호가 너무 길거나 짧습니다.")
    private String password;

    @NotBlank
    @Size(min = 4, max = 32, message = "이름이 너무 길거나 짧습니다.")
    private String name;

    @NotBlank
    @Pattern(regexp = "^01[0-9]-\\d{4}-\\d{4}$", message = "휴대폰 번호 형식이 올바르지 않습니다.")
    private String phoneNumber;
}