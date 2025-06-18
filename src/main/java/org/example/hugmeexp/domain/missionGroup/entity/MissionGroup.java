package org.example.hugmeexp.domain.missionGroup.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.hugmeexp.global.entity.BaseEntity;
import org.example.hugmeexp.global.entity.User;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class MissionGroup extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "teacher_id")
    private User teacher;
    @Column(nullable = false, length = 127)
    private String name;
}
