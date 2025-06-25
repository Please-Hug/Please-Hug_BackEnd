package org.example.hugmeexp.domain.qeust.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hugmeexp.domain.qeust.exception.AlreadyCompletedQuestException;
import org.example.hugmeexp.domain.user.entity.User;

@Getter
@Entity
@Table(name = "user_quest")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserQuest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quest_id", nullable = false)
    private Quest quest;

    @Column(nullable = false)
    private boolean isCompleted = false;

    @Builder
    private UserQuest(User user, Quest quest) {
        this.user = user;
        this.quest = quest;

    }

    // 정적 팩토리 메서드
    public static UserQuest createUserQuest(User user, Quest quest) {
        return UserQuest.builder()
                .user(user)
                .quest(quest)
                .build();
    }

    public void complete() {
        if (this.isCompleted) {
            throw new AlreadyCompletedQuestException(this.id);
        }
        this.isCompleted = true;
    }

    public void reset() {
        this.isCompleted = false;
    }
}