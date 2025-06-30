package org.example.hugmeexp.domain.bookmark.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.example.hugmeexp.global.entity.BaseEntity;
import org.example.hugmeexp.domain.user.entity.User;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "bookmark")
public class Bookmark extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 100, message = "제목은 100자를 초과할 수 없습니다")
    @Column(name = "title", nullable = false)
    private String title;

    @NotBlank(message = "링크는 필수입니다")
    @Pattern(regexp = "^https?://.*", message = "올바른 URL 형식이 아닙니다")
    @Column(name = "link", nullable = false)
    private String link;

    public void update(String title, String link) {
        this.title = title;
        this.link  = link;
    }
}