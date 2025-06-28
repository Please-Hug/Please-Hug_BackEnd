package org.example.hugmeexp.domain.qeust.repository;

import org.example.hugmeexp.domain.qeust.entity.UserQuest;
import org.example.hugmeexp.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserQuestRepository extends JpaRepository<UserQuest, Long> {

    List<UserQuest> findAllByUser(User user);
    Optional<UserQuest> findByUserAndId(User user, Long userQuestId);
}
