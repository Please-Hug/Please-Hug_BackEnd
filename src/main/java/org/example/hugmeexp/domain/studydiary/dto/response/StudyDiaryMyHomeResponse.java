package org.example.hugmeexp.domain.studydiary.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyDiaryMyHomeResponse {

    private Long id;

    private String title;

    private LocalDateTime createdAt;

    private Long daysAgo;
} 