package org.example.hugmeexp.domain.studydiary.dto.response;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.hugmeexp.domain.studydiary.entity.StudyDiaryComment;
import org.example.hugmeexp.domain.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyDiaryFindAllResponse {

    private Long id;

    private String name;

    private String title;

    private String content;

    private int likeNum;

    private int commentNum;

    private LocalDateTime createdAt;
}
