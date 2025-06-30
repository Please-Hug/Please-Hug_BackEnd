package org.example.hugmeexp.domain.praise.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.hugmeexp.domain.praise.dto.CommentEmojiReactionRequestDTO;
import org.example.hugmeexp.domain.praise.dto.CommentEmojiReactionResponseDTO;
import org.example.hugmeexp.domain.praise.entity.PraiseComment;
import org.example.hugmeexp.domain.praise.entity.CommentEmojiReaction;
import org.example.hugmeexp.domain.praise.exception.*;
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


        // 이모지 형식 검증
        String emoji = commentEmojiReactionRequestDTO.getEmoji();
        if(!isValidEmoji(emoji)){
            throw new InvalidEmojiException();
        }

        // 칭찬 존재 여부 확인
        if (!praiseRepository.existsById(praiseId)) {
            throw new PraiseNotFoundException();
        }

        // 댓글 존재 여부 확인
        PraiseComment comment = commentRepository.findById(commentId).orElseThrow(()-> new CommentNotFoundException());

        // 댓글이 해당 칭찬에 속하는지 확인
        if (!comment.getPraise().getId().equals(praiseId)){
            throw new MismatchedPraiseCommentException();
        }

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

    /* 댓글 반응 삭제 */
    @Transactional
    public void deleteCommentReaction(Long praiseId, Long commentId, String emojiChar, User user) {


        PraiseComment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);
        // 이모지 반응 존재 확인
        CommentEmojiReaction commentEmojiReaction = commentEmojiReactionRepository.findByCommentAndEmoji(comment, emojiChar);


        //PraiseComment comment = commentEmojiReaction.getComment();

        // 댓글과 반응 연결 확인
        if(!commentEmojiReaction.getComment().getId().equals(commentId)){
            throw new MismatchedCommentReactionException();
        }

        // 댓글이 해당 칭찬에 속하는지 확인
        if(!commentEmojiReaction.getComment().getPraise().getId().equals(praiseId)){
            throw new MismatchedPraiseCommentException();
        }

        // 작성자 본인 확인
        if(!commentEmojiReaction.getReactorWriter().getId().equals(user.getId())){
            throw new UnauthorizedEmojiDeleteException();
        }

        //commentEmojiReactionRepository.deleteByComment(comment);

        // 삭제
        commentEmojiReactionRepository.delete(commentEmojiReaction);
    }
}
