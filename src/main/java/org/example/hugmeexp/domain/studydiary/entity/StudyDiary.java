package org.example.hugmeexp.domain.studydiary.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.hugmeexp.domain.user.entity.User;
import org.example.hugmeexp.global.entity.BaseEntity;

import java.util.List;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyDiary extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="studydiary_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "studyDiary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudyDiaryComment> comments;

    private String title;

    private String content;

    private boolean isCreated;

    private int likeCount;

    // 수정 메소드들 (더티 체킹 활용)
    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateContent(String content) {
        this.content = content;
    }
}
