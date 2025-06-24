package org.example.hugmeexp.domain.studydiary.repository;

import org.example.hugmeexp.domain.studydiary.entity.StudyDiaryComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface StudyDiaryCommentRepository extends JpaRepository<StudyDiaryComment, Long> {

} 