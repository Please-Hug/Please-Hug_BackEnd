package org.example.hugmeexp.domain.praise.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.hugmeexp.domain.praise.dto.CommentEmojiReactionRequestDTO;
import org.example.hugmeexp.domain.praise.dto.CommentEmojiReactionResponseDTO;
import org.example.hugmeexp.domain.praise.entity.PraiseComment;
import org.example.hugmeexp.domain.praise.entity.Praise;
import org.example.hugmeexp.domain.praise.entity.CommentEmojiReaction;
import org.example.hugmeexp.domain.praise.exception.CommentNotFoundException;
import org.example.hugmeexp.domain.praise.exception.DuplicateEmojiReactionException;
import org.example.hugmeexp.domain.praise.exception.InvalidEmojiException;
import org.example.hugmeexp.domain.praise.exception.PraiseNotFoundException;
import org.example.hugmeexp.domain.praise.mapper.CommentEmojiReactionMapper;
import org.example.hugmeexp.domain.praise.repository.CommentEmojiReactionRepository;
import org.example.hugmeexp.domain.praise.repository.CommentRepository;
import org.example.hugmeexp.domain.praise.repository.PraiseRepository;
import org.example.hugmeexp.domain.praise.util.EmojiUtil;
import org.example.hugmeexp.domain.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentEmojiReactionService {

    private final CommentEmojiReactionRepository commentEmojiReactionRepository;
    private final PraiseRepository praiseRepository;
    private final CommentRepository commentRepository;
    private final CommentEmojiReactionMapper commentEmojiReactionMapper;

    /* 이모지 유효성 검사 */
    private boolean isValidEmoji(String input) {
        return EmojiUtil.isOnlyEmoji(input);
    }

    /* 댓글 반응 생성 */
    @Transactional
    public CommentEmojiReactionResponseDTO createCommentReaction(Long praiseId, Long commentId, @Valid CommentEmojiReactionRequestDTO commentEmojiReactionRequestDTO, User user) {

        // 칭찬 존재 여부 확인
        Praise praise = praiseRepository.findById(praiseId).orElseThrow(()->new PraiseNotFoundException());

        // 이모지 형식 검증
        String emoji = commentEmojiReactionRequestDTO.getEmoji();
        if(!isValidEmoji(emoji)){
            throw new InvalidEmojiException();
        }

        // 댓글 존재 여부 확인
        PraiseComment comment = commentRepository.findById(commentId).orElseThrow(()-> new CommentNotFoundException());

        // 반응 중복 확인
        boolean alreadyExists = commentEmojiReactionRepository.existsByCommentAndReactorWriterAndEmoji(comment,user,commentEmojiReactionRequestDTO.getEmoji());
        if(alreadyExists){
            throw new DuplicateEmojiReactionException();
        }

        // 반응 생성 및 저장
        CommentEmojiReaction commentEmojiReaction = commentEmojiReactionMapper.toEntity(commentEmojiReactionRequestDTO,comment,user);

        commentEmojiReactionRepository.save(commentEmojiReaction);

        return CommentEmojiReactionResponseDTO.from(commentEmojiReaction);

    }
}
