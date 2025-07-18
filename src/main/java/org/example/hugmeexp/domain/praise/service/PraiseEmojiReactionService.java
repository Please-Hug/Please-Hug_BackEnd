package org.example.hugmeexp.domain.praise.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.praise.dto.PraiseEmojiReactionRequestDTO;
import org.example.hugmeexp.domain.praise.dto.PraiseEmojiReactionResponseDTO;
import org.example.hugmeexp.domain.praise.entity.Praise;
import org.example.hugmeexp.domain.praise.entity.PraiseEmojiReaction;
import org.example.hugmeexp.domain.praise.exception.*;
import org.example.hugmeexp.domain.praise.mapper.PraiseEmojiReactionMapper;
import org.example.hugmeexp.domain.praise.repository.PraiseEmojiReactionRepository;
import org.example.hugmeexp.domain.praise.repository.PraiseRepository;
import org.example.hugmeexp.domain.praise.util.EmojiUtil;
import org.example.hugmeexp.domain.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PraiseEmojiReactionService {

    private final PraiseEmojiReactionRepository praiseEmojiReactionRepository;
    private final PraiseRepository praiseRepository;
    private final PraiseEmojiReactionMapper praiseEmojiReactionMapper;

    /* 이모지 유효성 검사 */
    private boolean isValidEmoji(String input) {
        return EmojiUtil.isOnlyEmoji(input);
    }

    /* 칭찬 게시물에 반응 생성 */
    @Transactional
    public PraiseEmojiReactionResponseDTO addEmojiReaction(Long praiseId, User user, PraiseEmojiReactionRequestDTO praiseEmojiReactionRequestDTO) {

        // praise 조회
        Praise praise = praiseRepository.findById(praiseId).orElseThrow(() -> new PraiseNotFoundException());

        // 이모지 형식 검증
        String emoji = praiseEmojiReactionRequestDTO.getEmoji();
        if(!isValidEmoji(emoji)){
            throw new InvalidEmojiException();
        }

        // 동일 사용자 중복 반응 체크
        boolean alreadyReacted = praiseEmojiReactionRepository.existsByPraiseAndReactorWriterAndEmoji(praise,user,praiseEmojiReactionRequestDTO.getEmoji());
        if(alreadyReacted){
            throw new DuplicateEmojiReactionException();
        }

        // 이모지 생성
        PraiseEmojiReaction praiseEmojiReaction = praiseEmojiReactionMapper.toEntity(praise,user,praiseEmojiReactionRequestDTO);

        // 저장
        PraiseEmojiReaction saved = praiseEmojiReactionRepository.save(praiseEmojiReaction);

        // 동일한 이모지 반응자 전체 조회
        List<PraiseEmojiReaction> sameEmojiReactions = praiseEmojiReactionRepository.findByPraiseAndEmoji(praise,saved.getEmoji());

        return PraiseEmojiReactionResponseDTO.from(saved, sameEmojiReactions);
    }

    /* 칭찬 게시물에 반응 삭제 */
    @Transactional
    public void deleteEmojiReaction(Long praiseId, Long emojiId, User user) {

        // 이모지 반응 조회
        PraiseEmojiReaction reaction = praiseEmojiReactionRepository.findById(emojiId)
                .orElseThrow(() -> new PraiseEmojiReactionNotFoundException());

        // URL 의 praiseId 와 실제 반응의 praiseId 일치 여부 확인
        if(!reaction.getPraise().getId().equals(praiseId)){
            throw new InvalidPraiseEmojiAccessException();
        }

        // 본인만 삭제할 수 있도록 작성자 확인
        if (!reaction.getReactorWriter().getId().equals(user.getId())){
            throw new UnauthorizedEmojiDeleteException();
        }

        praiseEmojiReactionRepository.delete(reaction);
    }
}
