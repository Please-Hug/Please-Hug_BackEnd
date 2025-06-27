package org.example.hugmeexp.domain.studydiary.repository;

import org.example.hugmeexp.domain.studydiary.entity.StudyDiaryLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface StudyDiaryLikeRepository extends JpaRepository<StudyDiaryLike, Long> {
}
