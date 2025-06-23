package org.example.hugmeexp.domain.missionGroup.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.hugmeexp.global.entity.BaseEntity;
import org.example.hugmeexp.domain.user.entity.User;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "mission_group_id"})})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserMissionGroup extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne
    @JoinColumn(name = "mission_group_id", nullable = false)
    private MissionGroup missionGroup;
}
