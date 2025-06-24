package org.example.hugmeexp.domain.praise.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.praise.dto.PraiseEmojiReactionRequestDTO;
import org.example.hugmeexp.domain.praise.dto.PraiseEmojiReactionResponseDTO;
import org.example.hugmeexp.domain.praise.entity.Praise;
import org.example.hugmeexp.domain.praise.entity.PraiseEmojiReaction;
import org.example.hugmeexp.domain.praise.exception.PraiseNotFoundException;
import org.example.hugmeexp.domain.praise.mapper.PraiseEmojiReactionMapper;
import org.example.hugmeexp.domain.praise.repository.PraiseEmojiReactionRepository;
import org.example.hugmeexp.domain.praise.repository.PraiseRepository;
import org.example.hugmeexp.domain.user.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PraiseEmojiReactionService {

    private final PraiseEmojiReactionRepository praiseEmojiReactionRepository;
    private final PraiseRepository praiseRepository;
    private final PraiseEmojiReactionMapper praiseEmojiReactionMapper;

    /* 칭찬 게시물에 반응 생성 */
    public PraiseEmojiReactionResponseDTO addEmojiReaction(Long praiseId, User user, PraiseEmojiReactionRequestDTO praiseEmojiReactionRequestDTO) {

        // praise 조회
        Praise praise = praiseRepository.findById(praiseId).orElseThrow(() -> new PraiseNotFoundException());

        // 이모지 생성
        PraiseEmojiReaction praiseEmojiReaction = praiseEmojiReactionMapper.toEntity(praise,user,praiseEmojiReactionRequestDTO);

        // 저장
        PraiseEmojiReaction saved = praiseEmojiReactionRepository.save(praiseEmojiReaction);

        // 동일한 이모지 반응자 전체 조회
        List<PraiseEmojiReaction> sameEmojiReactions = praiseEmojiReactionRepository.findByPraiseAndEmoji(praise,saved.getEmoji());

        return PraiseEmojiReactionResponseDTO.from(saved, sameEmojiReactions);
    }

}
