package org.example.hugmeexp.domain.studydiary.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.hugmeexp.global.entity.BaseEntity;
import org.example.hugmeexp.global.entity.User;

import java.util.List;

@Getter
@Entity
@Builder
@RequiredArgsConstructor
public class StudyDiary extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="studydiary_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "studydiary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudyDiaryComment> comments;

    private String title;

    private String content;

    private boolean isCreated;

    private int like;
}
