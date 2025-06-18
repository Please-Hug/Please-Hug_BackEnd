package org.example.hugmeexp.domain.praise.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.hugmeexp.global.entity.BaseEntity;
import org.example.hugmeexp.global.entity.User;

// 댓글
@Entity
@Getter
@NoArgsConstructor
@Table(name = "praise_comment")
public class PraiseComment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    // PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "praise_id", nullable = false)
    private Praise praise;    // 어떤 칭찬글에 달린 댓글인지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_writer_id", nullable = false)
    private User commentWriter;    // 댓글 작성자 이름

    @Column(nullable = false)
    private String content;    // 댓글 내용

}
