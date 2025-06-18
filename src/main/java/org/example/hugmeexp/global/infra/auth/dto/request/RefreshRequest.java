package org.example.hugmeexp.global.infra.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshRequest {
    private String accessToken;
    private String refreshToken;
}
