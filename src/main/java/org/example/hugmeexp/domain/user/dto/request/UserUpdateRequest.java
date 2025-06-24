package org.example.hugmeexp.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class UserUpdateRequest {

    @NotBlank
    @Size(min = 2, max = 32)
    private String name;

    @Size(max = 255)
    private String description;

    @NotBlank
    @Pattern(regexp = "^01[0-9]-\\d{4}-\\d{4}$")
    private String phoneNumber;
}
