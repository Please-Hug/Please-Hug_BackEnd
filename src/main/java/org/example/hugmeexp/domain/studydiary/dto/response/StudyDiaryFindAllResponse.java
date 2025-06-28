package org.example.hugmeexp.domain.studydiary.dto.response;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import org.example.hugmeexp.domain.studydiary.entity.StudyDiaryComment;
import org.example.hugmeexp.domain.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class StudyDiaryFindAllResponse {

    private Long id;

    private String userName;

    private String title;

    private String content;

    private int likeNum;

    private int commentNum;

    private LocalDateTime createdAt;
}
