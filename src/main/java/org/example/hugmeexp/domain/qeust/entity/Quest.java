package org.example.hugmeexp.domain.qeust.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hugmeexp.domain.qeust.dto.QuestRequest;
import org.example.hugmeexp.domain.qeust.enums.QuestType;

@Getter
@Entity
@Table(name = "quest")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Quest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private boolean isDeleted = false;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private QuestType type;

    @Builder
    private Quest(String name, String url, QuestType type) {
        this.name = name;
        this.url = url;
        this.type = type;
    }

    // 정적 팩토리 메서드
    public static Quest createQuest(String name, String url, QuestType type) {
        return Quest.builder()
                .name(name)
                .url(url)
                .type(type)
                .build();
    }

    public void updateQuest(QuestRequest dto) {
        this.name = dto.getName();
        this.url = dto.getUrl();
    }

    public void delete() {
        this.isDeleted = true;
    }
}
