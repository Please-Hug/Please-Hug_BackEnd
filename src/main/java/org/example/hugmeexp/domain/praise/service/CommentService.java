package org.example.hugmeexp.domain.praise.service;

import lombok.RequiredArgsConstructor;
import org.example.hugmeexp.domain.praise.dto.CommentRequestDTO;
import org.example.hugmeexp.domain.praise.dto.CommentResponseDTO;
import org.example.hugmeexp.domain.praise.entity.PraiseComment;
import org.example.hugmeexp.domain.praise.entity.Praise;
import org.example.hugmeexp.domain.praise.exception.PraiseNotFoundException;
import org.example.hugmeexp.domain.praise.mapper.CommentMapper;
import org.example.hugmeexp.domain.praise.repository.CommentRepository;
import org.example.hugmeexp.domain.praise.repository.PraiseRepository;
import org.example.hugmeexp.domain.user.entity.User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    public final CommentMapper commentMapper;
    public final CommentRepository commentRepository;
    public final PraiseRepository praiseRepository;

    /* 댓글 작성 */
    public CommentResponseDTO createComment(Long praiseId, CommentRequestDTO commentRequestDTO, User commentWriter) {

        // Praise 엔티티 조회
        Praise praise = praiseRepository.findById(praiseId).orElseThrow(() -> new PraiseNotFoundException());

        // DTO -> Entity 변환
        PraiseComment comment = commentMapper.toEntity(commentRequestDTO, praise, commentWriter);

        // DB 저장
        PraiseComment saved = commentRepository.save(comment);

        // Entity -> DTO 변환
        return commentMapper.toDTO(saved);
    }

    /* 댓글 삭제 */
    public void deleteComment(Long commentId) {

        commentRepository.deleteById(commentId);
    }
}
