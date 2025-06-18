package org.example.hugmeexp.domain.praise.repository;

import org.example.hugmeexp.domain.praise.entity.Praise;
import org.example.hugmeexp.global.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PraiseRepository extends JpaRepository<Praise, Long> {
    List<Praise> findByCreatedAtBetween(LocalDateTime createdAtAfter, LocalDateTime createdAtBefore);
//    List<Praise> findBySenderIdContainingOrReceiverIdContaining(User senderId, User receiverId);
}
