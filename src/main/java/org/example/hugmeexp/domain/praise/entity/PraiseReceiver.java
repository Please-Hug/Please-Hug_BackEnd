package org.example.hugmeexp.domain.praise.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hugmeexp.domain.user.entity.User;
import org.example.hugmeexp.global.entity.BaseEntity;

// 다수의 유저에게 칭찬 보내기 위한 entity
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "praiseReceiver")
public class PraiseReceiver extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    // PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "praise_id")
    private Praise praise;    // 연결된 칭찬 엔티티

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private User receiver;    // 칭찬 받는 사람
}
