package org.example.hugmeexp.domain.praise.service;

import lombok.RequiredArgsConstructor;
import org.example.hugmeexp.domain.praise.dto.CommentRequestDTO;
import org.example.hugmeexp.domain.praise.dto.CommentResponseDTO;
import org.example.hugmeexp.domain.praise.entity.PraiseComment;
import org.example.hugmeexp.domain.praise.entity.Praise;
import org.example.hugmeexp.domain.praise.exception.CommentNotFoundException;
import org.example.hugmeexp.domain.praise.exception.PraiseNotFoundException;
import org.example.hugmeexp.domain.praise.mapper.CommentMapper;
import org.example.hugmeexp.domain.praise.repository.CommentRepository;
import org.example.hugmeexp.domain.praise.repository.PraiseRepository;
import org.example.hugmeexp.domain.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final PraiseRepository praiseRepository;

    /* 댓글 작성 */
    @Transactional
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
    @Transactional
    public void deleteComment(Long commentId) {

        // 댓글 존재 여부 확인
        if (!commentRepository.existsById(commentId)){
            throw new CommentNotFoundException();
        }
        commentRepository.deleteById(commentId);
    }
}
