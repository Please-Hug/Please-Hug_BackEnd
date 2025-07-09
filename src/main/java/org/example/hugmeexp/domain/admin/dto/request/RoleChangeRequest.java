// src/main/java/org/example/hugmeexp/domain/admin/dto/request/RoleChangeRequest.java
package org.example.hugmeexp.domain.admin.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hugmeexp.domain.user.enums.UserRole;

/**
 * 관리자가 특정 회원의 역할(Role)을 변경할 때 사용하는 요청 DTO
 */

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleChangeRequest {

    @Schema(
            description = "변경할 권한 (USER, LECTURER, ADMIN 중 하나)",
            example = "LECTURER",
            allowableValues = {"USER", "LECTURER", "ADMIN"}
    )
    @NotNull
    private UserRole role;

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}
