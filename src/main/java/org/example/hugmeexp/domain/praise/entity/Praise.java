package org.example.hugmeexp.domain.praise.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.hugmeexp.domain.praise.enums.PraiseType;
import org.example.hugmeexp.global.entity.BaseEntity;
import org.example.hugmeexp.global.entity.User;

// 칭찬
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "praise")
public class Praise extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    // PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;    // 칭찬 보낸 사람 이름

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;    // 칭찬 받은 사람 이름

    @Column(nullable = false)
    private String content;    // 칭찬 내용

    @Enumerated(EnumType.STRING)
    @Column(name = "praise_type", nullable = false)
    private PraiseType praiseType;    // 칭찬 타입


}
