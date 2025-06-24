package org.example.hugmeexp.domain.studydiary.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.hugmeexp.domain.user.entity.User;
import org.example.hugmeexp.global.entity.BaseEntity;


@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class StudyDiaryComment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "studydiary_comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studydiary_id")
    private StudyDiary studyDiary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String content;
}
