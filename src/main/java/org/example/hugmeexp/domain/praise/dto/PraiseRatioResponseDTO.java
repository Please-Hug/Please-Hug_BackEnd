package org.example.hugmeexp.domain.praise.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hugmeexp.domain.praise.entity.Praise;
import org.example.hugmeexp.domain.praise.enums.PraiseType;

import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PraiseRatioResponseDTO {

    private PraiseType type;    // 칭찬 타입
    private int percentage;    // 비율

    public static PraiseRatioResponseDTO from(PraiseType type, int percentage){
        return PraiseRatioResponseDTO.builder()
                .type(type)
                .percentage(percentage)
                .build();
    }
}
