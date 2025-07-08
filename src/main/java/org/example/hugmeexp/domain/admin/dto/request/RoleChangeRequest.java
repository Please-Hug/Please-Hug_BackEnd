// src/main/java/org/example/hugmeexp/domain/admin/dto/request/RoleChangeRequest.java
package org.example.hugmeexp.domain.admin.dto.request;

import jakarta.validation.constraints.NotNull;
import org.example.hugmeexp.domain.user.enums.UserRole;

public class RoleChangeRequest {

    @NotNull
    private UserRole role;

    public RoleChangeRequest() {}

    public RoleChangeRequest(UserRole role) {
        this.role = role;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}