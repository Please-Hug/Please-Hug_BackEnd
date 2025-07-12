package org.example.hugmeexp.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
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

    @Schema(description = "사용자 이름 (2~32자)", example = "백엔드 3회차(홍길동)")
    @NotBlank
    @Size(min = 2, max = 32)
    private String name;

    @Schema(description = "자기소개 (최대 255자)", example = "안녕하세요. 백엔드 개발자 홍길동입니다.")
    @Size(max = 255)
    private String description;

    @Schema(description = "전화번호 (하이픈 포함)", example = "010-1234-5678", pattern = "^01[0-9]-\\d{4}-\\d{4}$")
    @NotBlank
    @Pattern(regexp = "^01[0-9]-\\d{4}-\\d{4}$")
    private String phoneNumber;

    @Schema(description = "경험치 직접 설정 (0 이상)", example = "1500")
    @Min(value = 0, message = "경험치는 0 이상이어야 합니다.")
    private Integer exp;

    @Schema(description = "포인트 직접 설정 (0 이상)", example = "500")
    @Min(value = 0, message = "포인트는 0 이상이어야 합니다.")
    private Integer point;
}
