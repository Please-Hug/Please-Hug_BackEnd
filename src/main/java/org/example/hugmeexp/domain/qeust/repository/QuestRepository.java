package org.example.hugmeexp.domain.qeust.repository;

import org.example.hugmeexp.domain.qeust.entity.Quest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestRepository extends JpaRepository<Quest, Long> {
    List<Quest> findAllByIsDeletedFalse();
}
