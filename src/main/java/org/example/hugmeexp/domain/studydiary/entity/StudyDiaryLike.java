package org.example.hugmeexp.domain.studydiary.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.hugmeexp.global.entity.BaseEntity;
import org.example.hugmeexp.global.entity.User;

@Entity
@Getter
@RequiredArgsConstructor
@Builder
public class StudyDiaryLike extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "studydiary_like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studydiary_id")
    private StudyDiary studyDiary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
