package org.example.hugmeexp.domain.mission.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"user_mission_id"})})
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @Setter
    @JoinColumn(name = "user_mission_id", nullable = false)
    private UserMission userMission;

    @Column(nullable = false)
    @Setter
    private String fileName;

    @Column(nullable = false)
    @Setter
    private String originalFileName;

    @Column(nullable = false, length = 65535)
    private String comment;

    @Setter
    @Column(length = 65535)
    private String feedback;
}
