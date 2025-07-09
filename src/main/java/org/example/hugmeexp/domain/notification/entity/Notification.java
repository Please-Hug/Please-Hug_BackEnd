package org.example.hugmeexp.domain.notification.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hugmeexp.domain.notification.enums.NotificationType;
import org.example.hugmeexp.domain.user.entity.User;
import org.example.hugmeexp.global.entity.BaseEntity;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "notification")
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 알림 받을 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private boolean isRead;

    @Column(name ="target_id", nullable = false)
    private Long targetId; // 알림이 연결된 대상 ID (예: 배움일기, 칭찬 등)

    public void markAsRead() {
        this.isRead = true;
    }

    public static Notification of(User user, NotificationType type, String content, Long targetId) {
        return Notification.builder()
                .user(user)
                .type(type)
                .content(content)
                .targetId(targetId)
                .isRead(false)
                .build();
    }
}
