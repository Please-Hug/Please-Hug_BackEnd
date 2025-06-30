package org.example.hugmeexp.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hugmeexp.domain.user.enums.UserRole;

@Getter
@NoArgsConstructor
public class ChangeRoleRequest {

    @Schema(description = "권한을 변경할 대상 유저의 username", example = "seungwook0924")
    @NotBlank
    private String username;

    @Schema(
            description = "변경할 권한 (USER, LECTURER, ADMIN 중 하나)",
            example = "LECTURER",
            allowableValues = {"USER", "LECTURER", "ADMIN"}
    )
    @NotNull
    private UserRole role;

    public ChangeRoleRequest(String username, UserRole role) {
        this.username = username;
        this.role = role;
    }
}
