package org.example.hugmeexp.domain.studydiary.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.hugmeexp.domain.studydiary.exception.LikeNotFoundException;
import org.example.hugmeexp.domain.user.entity.User;
import org.example.hugmeexp.global.entity.BaseEntity;

import java.util.List;
import java.util.Optional;

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

    @OneToMany(mappedBy = "studyDiary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudyDiaryLike> likes;

    private String title;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    private boolean isCreated;

    private int likeCount;

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void updateLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int deleteLike(Long userId) {
        Optional<StudyDiaryLike> existingLike = this.likes.stream()
                .filter(like -> like.getUser().getId().equals(userId))
                .findFirst();

        if (existingLike.isPresent()) {
            StudyDiaryLike toRemove = existingLike.get();
            this.likes.remove(toRemove);
            this.likeCount--;
            return this.likeCount;
        } else {
            throw new LikeNotFoundException();
        }
    }

    public int addLike(User user) {
        StudyDiaryLike newLike = StudyDiaryLike.builder()
                .studyDiary(this)
                .user(user)
                .build();

        this.likes.add(newLike);
        this.likeCount++;

        return likeCount;
    }
}

