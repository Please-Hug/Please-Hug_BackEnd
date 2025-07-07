package org.example.hugmeexp.domain.praise.service;

import org.example.hugmeexp.domain.praise.dto.CommentEmojiReactionRequestDTO;
import org.example.hugmeexp.domain.praise.dto.CommentEmojiReactionResponseDTO;
import org.example.hugmeexp.domain.praise.dto.ReactionUserDTO;
import org.example.hugmeexp.domain.praise.entity.CommentEmojiReaction;
import org.example.hugmeexp.domain.praise.entity.Praise;
import org.example.hugmeexp.domain.praise.entity.PraiseComment;
import org.example.hugmeexp.domain.praise.exception.*;
import org.example.hugmeexp.domain.praise.mapper.CommentEmojiReactionMapper;
import org.example.hugmeexp.domain.praise.repository.CommentEmojiReactionRepository;
import org.example.hugmeexp.domain.praise.repository.CommentRepository;
import org.example.hugmeexp.domain.praise.repository.PraiseRepository;
import org.example.hugmeexp.domain.praise.util.EmojiUtil;
import org.example.hugmeexp.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommentEmojiReactionServiceTest {

    @InjectMocks
    private CommentEmojiReactionService commentEmojiReactionService;

    @Mock
    private CommentEmojiReactionRepository commentEmojiReactionRepository;

    @Mock
    private PraiseRepository praiseRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentEmojiReactionMapper commentEmojiReactionMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("이모지 반응 생성 성공 테스트")
    void testCreateCommentReaction_Success() {
        // given
        Long praiseId = 1L;
        Long commentId = 1L;
        String emojiValue = "😊";
        
        // 요청 DTO 생성
        CommentEmojiReactionRequestDTO requestDTO = CommentEmojiReactionRequestDTO.builder()
                .emoji(emojiValue)
                .build();
        
        // 사용자 객체 생성
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(user.getUsername()).thenReturn("testuser");
        when(user.getName()).thenReturn("Test User");
        
        // 칭찬 객체 생성
        Praise praise = Praise.builder()
                .id(praiseId)
                .build();
        
        // 댓글 객체 생성
        PraiseComment comment = PraiseComment.builder()
                .id(commentId)
                .praise(praise)
                .build();
        
        // 이모지 반응 객체 생성
        CommentEmojiReaction reaction = CommentEmojiReaction.builder()
                .id(1L)
                .comment(comment)
                .reactorWriter(user)
                .emoji(emojiValue)
                .build();
        
        // 응답 DTO 생성
        CommentEmojiReactionResponseDTO responseDTO = CommentEmojiReactionResponseDTO.builder()
                .id(1L)
                .commentId(commentId)
                .reactorName(ReactionUserDTO.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .name(user.getName())
                        .build())
                .emoji(emojiValue)
                .createdAt(LocalDateTime.now())
                .build();
        
        // mock 설정
        when(praiseRepository.existsById(praiseId)).thenReturn(true);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(commentEmojiReactionRepository.existsByCommentAndReactorWriterAndEmoji(comment, user, emojiValue)).thenReturn(false);
        when(commentEmojiReactionMapper.toEntity(requestDTO, comment, user)).thenReturn(reaction);
        when(commentEmojiReactionRepository.save(reaction)).thenReturn(reaction);
        
        // when
        CommentEmojiReactionResponseDTO result = commentEmojiReactionService.createCommentReaction(praiseId, commentId, requestDTO, user);
        
        // then
        assertNotNull(result, "이모지 반응 생성 결과는 null이 아니어야 합니다");
        
        // 메서드 호출 검증
        verify(praiseRepository).existsById(praiseId);
        verify(commentRepository).findById(commentId);
        verify(commentEmojiReactionRepository).existsByCommentAndReactorWriterAndEmoji(comment, user, emojiValue);
        verify(commentEmojiReactionMapper).toEntity(requestDTO, comment, user);
        verify(commentEmojiReactionRepository).save(reaction);
    }

    @Test
    @DisplayName("이모지 반응 생성 실패 - 중복 반응")
    void testCreateCommentReaction_DuplicateReaction() {
        // given
        Long praiseId = 1L;
        Long commentId = 1L;
        String emojiValue = "😊";
        
        // 요청 DTO 생성
        CommentEmojiReactionRequestDTO requestDTO = CommentEmojiReactionRequestDTO.builder()
                .emoji(emojiValue)
                .build();
        
        // 사용자 객체 생성
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        
        // 칭찬 객체 생성
        Praise praise = Praise.builder()
                .id(praiseId)
                .build();
        
        // 댓글 객체 생성
        PraiseComment comment = PraiseComment.builder()
                .id(commentId)
                .praise(praise)
                .build();
        
        // mock 설정
        when(praiseRepository.existsById(praiseId)).thenReturn(true);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        
        // 중복 반응 설정 - 핵심 테스트 부분
        when(commentEmojiReactionRepository.existsByCommentAndReactorWriterAndEmoji(comment, user, emojiValue)).thenReturn(true);
        
        // then
        assertThrows(DuplicateEmojiReactionException.class, () -> {
            // when
            commentEmojiReactionService.createCommentReaction(praiseId, commentId, requestDTO, user);
        });
        
        // 메서드 호출 검증
        verify(praiseRepository).existsById(praiseId);
        verify(commentRepository).findById(commentId);
        verify(commentEmojiReactionRepository).existsByCommentAndReactorWriterAndEmoji(comment, user, emojiValue);
        
        // 중복 반응이므로 아래 메서드들은 호출되지 않아야 함
        verifyNoInteractions(commentEmojiReactionMapper);
        verify(commentEmojiReactionRepository, never()).save(any(CommentEmojiReaction.class));
    }

    @Test
    @DisplayName("이모지 반응 생성 실패 - 유효하지 않은 이모지")
    void testCreateCommentReaction_InvalidEmoji() {
        // given
        Long praiseId = 1L;
        Long commentId = 1L;
        String invalidEmoji = "abc"; // 이모지가 아닌 일반 텍스트
        
        // 요청 DTO 생성
        CommentEmojiReactionRequestDTO requestDTO = CommentEmojiReactionRequestDTO.builder()
                .emoji(invalidEmoji)
                .build();
        
        // 사용자 객체 생성
        User user = mock(User.class);
        
        // then
        assertThrows(InvalidEmojiException.class, () -> {
            // when
            commentEmojiReactionService.createCommentReaction(praiseId, commentId, requestDTO, user);
        });
        
        // 메서드 호출 검증 - 이모지 검증에서 실패하므로 다른 메서드들은 호출되지 않아야 함
        verifyNoInteractions(praiseRepository);
        verifyNoInteractions(commentRepository);
        verifyNoInteractions(commentEmojiReactionRepository);
        verifyNoInteractions(commentEmojiReactionMapper);
    }

    @Test
    @DisplayName("이모지 반응 생성 실패 - 칭찬 ID 없음")
    void testCreateCommentReaction_PraiseNotFound() {
        // given
        Long invalidPraiseId = 999L;
        Long commentId = 1L;
        String emojiValue = "😊";
        
        // 요청 DTO 생성
        CommentEmojiReactionRequestDTO requestDTO = CommentEmojiReactionRequestDTO.builder()
                .emoji(emojiValue)
                .build();
        
        // 사용자 객체 생성
        User user = mock(User.class);
        
        // mock 설정
        when(praiseRepository.existsById(invalidPraiseId)).thenReturn(false);
        
        // then
        assertThrows(PraiseNotFoundException.class, () -> {
            // when
            commentEmojiReactionService.createCommentReaction(invalidPraiseId, commentId, requestDTO, user);
        });
        
        // 메서드 호출 검증
        verify(praiseRepository).existsById(invalidPraiseId);
        verifyNoInteractions(commentRepository);
        verifyNoInteractions(commentEmojiReactionRepository);
        verifyNoInteractions(commentEmojiReactionMapper);
    }

    @Test
    @DisplayName("이모지 반응 생성 실패 - 댓글 ID 없음")
    void testCreateCommentReaction_CommentNotFound() {
        // given
        Long praiseId = 1L;
        Long invalidCommentId = 999L;
        String emojiValue = "😊";
        
        // 요청 DTO 생성
        CommentEmojiReactionRequestDTO requestDTO = CommentEmojiReactionRequestDTO.builder()
                .emoji(emojiValue)
                .build();
        
        // 사용자 객체 생성
        User user = mock(User.class);
        
        // mock 설정
        when(praiseRepository.existsById(praiseId)).thenReturn(true);
        when(commentRepository.findById(invalidCommentId)).thenReturn(Optional.empty());
        
        // then
        assertThrows(CommentNotFoundException.class, () -> {
            // when
            commentEmojiReactionService.createCommentReaction(praiseId, invalidCommentId, requestDTO, user);
        });
        
        // 메서드 호출 검증
        verify(praiseRepository).existsById(praiseId);
        verify(commentRepository).findById(invalidCommentId);
        verifyNoInteractions(commentEmojiReactionRepository);
        verifyNoInteractions(commentEmojiReactionMapper);
    }

    @Test
    @DisplayName("이모지 반응 생성 실패 - 댓글이 해당 칭찬에 속하지 않음")
    void testCreateCommentReaction_MismatchedPraiseComment() {
        // given
        Long praiseId = 1L;
        Long commentId = 1L;
        Long differentPraiseId = 2L;
        String emojiValue = "😊";
        
        // 요청 DTO 생성
        CommentEmojiReactionRequestDTO requestDTO = CommentEmojiReactionRequestDTO.builder()
                .emoji(emojiValue)
                .build();
        
        // 사용자 객체 생성
        User user = mock(User.class);
        
        // 다른 칭찬 객체 생성
        Praise differentPraise = Praise.builder()
                .id(differentPraiseId)
                .build();
        
        // 댓글 객체 생성 (다른 칭찬에 속함)
        PraiseComment comment = PraiseComment.builder()
                .id(commentId)
                .praise(differentPraise)
                .build();
        
        // mock 설정
        when(praiseRepository.existsById(praiseId)).thenReturn(true);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        
        // then
        assertThrows(MismatchedPraiseCommentException.class, () -> {
            // when
            commentEmojiReactionService.createCommentReaction(praiseId, commentId, requestDTO, user);
        });
        
        // 메서드 호출 검증
        verify(praiseRepository).existsById(praiseId);
        verify(commentRepository).findById(commentId);
        verifyNoInteractions(commentEmojiReactionRepository);
        verifyNoInteractions(commentEmojiReactionMapper);
    }
}