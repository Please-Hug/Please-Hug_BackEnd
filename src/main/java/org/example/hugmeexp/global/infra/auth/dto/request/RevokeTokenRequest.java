package org.example.hugmeexp.global.infra.auth.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class RevokeTokenRequest {
    private String token;
}
