package org.example.hugmeexp.domain.studydiary.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class StudyDiaryMyHomeResponse {

    private Long id;

    private String title;

    private LocalDateTime createdAt;

    private Long daysAgo;
} 