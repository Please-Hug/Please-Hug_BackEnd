package org.example.hugmeexp.domain.praise.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.hugmeexp.global.entity.BaseEntity;
import org.example.hugmeexp.global.entity.User;

// 댓글반응
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder(toBuilder = true)
@Table(name = "comment_emoji_reaction")
public class CommentEmojiReaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    // PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private PraiseComment commentId;    // 댓글에 반응하기

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reaction_writer_id", nullable = false)
    private User reactorWriterId;    // 반응한 사람 이름

    @Column(nullable = false, length = 1)
    private String emoji;    // 이모지 값

}
