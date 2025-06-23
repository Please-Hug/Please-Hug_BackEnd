package org.example.hugmeexp.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hugmeexp.domain.user.enums.UserRole;

@Getter
@NoArgsConstructor
public class ChangeRoleRequest {

    @NotBlank
    private String username;

    @NotNull
    private UserRole role;

    public ChangeRoleRequest(String username, UserRole role) {
        this.username = username;
        this.role = role;
    }
}
