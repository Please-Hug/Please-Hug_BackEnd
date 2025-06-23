package org.example.hugmeexp.global.infra.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank
    @Size(min = 4, max = 32)
    private String username;

    @NotBlank
    @Size(min = 8, max = 60)
    private String password;

    @NotBlank
    @Size(min = 2, max = 32)
    private String name;

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