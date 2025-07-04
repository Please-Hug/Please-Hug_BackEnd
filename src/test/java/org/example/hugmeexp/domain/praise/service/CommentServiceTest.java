package org.example.hugmeexp.domain.praise.service;

import org.example.hugmeexp.domain.praise.dto.CommentRequestDTO;
import org.example.hugmeexp.domain.praise.dto.CommentResponseDTO;
import org.example.hugmeexp.domain.praise.entity.Praise;
import org.example.hugmeexp.domain.praise.entity.PraiseComment;
import org.example.hugmeexp.domain.praise.exception.CommentNotFoundException;
import org.example.hugmeexp.domain.praise.exception.ForbiddenCommentAccessException;
import org.example.hugmeexp.domain.praise.exception.PraiseNotFoundException;
import org.example.hugmeexp.domain.praise.mapper.CommentMapper;
import org.example.hugmeexp.domain.praise.repository.CommentEmojiReactionRepository;
import org.example.hugmeexp.domain.praise.repository.CommentRepository;
import org.example.hugmeexp.domain.praise.repository.PraiseRepository;
import org.example.hugmeexp.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PraiseRepository praiseRepository;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private CommentEmojiReactionRepository commentEmojiReactionRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("댓글 작성 성공 테스트")
    void testCreateComment_Success() {
        // given
        Long praiseId = 1L;
        CommentRequestDTO requestDTO = CommentRequestDTO.builder()
                .content("테스트 댓글 내용")
                .build();

        // User 객체는 mock으로 대체
        User commentWriter = mock(User.class);
        when(commentWriter.getId()).thenReturn(1L);
        when(commentWriter.getName()).thenReturn("테스트 작성자");

        // Praise 객체는 builder로 생성
        Praise praise = Praise.builder()
                .id(praiseId)
                .content("테스트 칭찬 내용")
                .build();

        // PraiseComment 객체는 builder로 생성
        PraiseComment comment = PraiseComment.builder()
                .id(1L)
                .content(requestDTO.getContent())
                .commentWriter(commentWriter)
                .praise(praise)
                .build();

        // CommentResponseDTO 객체는 builder로 생성
        CommentResponseDTO responseDTO = CommentResponseDTO.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .commenterName(commentWriter.getName())
                .build();

        // mock 설정
        when(praiseRepository.findById(praiseId)).thenReturn(Optional.of(praise));
        when(commentMapper.toEntity(requestDTO, praise, commentWriter)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.toDTO(comment)).thenReturn(responseDTO);

        // when
        CommentResponseDTO result = commentService.createComment(praiseId, requestDTO, commentWriter);

        // then
        assertNotNull(result, "댓글 작성 결과는 null이 아니어야 합니다");
        assertEquals(responseDTO.getId(), result.getId(), "댓글 ID가 일치해야 합니다");
        assertEquals(responseDTO.getContent(), result.getContent(), "댓글 내용이 일치해야 합니다");
        assertEquals(responseDTO.getCommenterName(), result.getCommenterName(), "댓글 작성자 이름이 일치해야 합니다");

        // 메서드 호출 검증
        verify(praiseRepository).findById(praiseId);
        verify(commentMapper).toEntity(requestDTO, praise, commentWriter);
        verify(commentRepository).save(comment);
        verify(commentMapper).toDTO(comment);
    }

    @Test
    @DisplayName("댓글 작성 실패 - 칭찬 ID 없음")
    void testCreateComment_PraiseNotFound() {
        // given
        Long invalidPraiseId = 999L;
        CommentRequestDTO requestDTO = CommentRequestDTO.builder()
                .content("테스트 댓글 내용")
                .build();

        User commentWriter = mock(User.class);

        // mock 설정
        when(praiseRepository.findById(invalidPraiseId)).thenReturn(Optional.empty());

        // then
        assertThrows(PraiseNotFoundException.class, () -> {
            // when
            commentService.createComment(invalidPraiseId, requestDTO, commentWriter);
        });

        // 메서드 호출 검증
        verify(praiseRepository).findById(invalidPraiseId);
        verifyNoInteractions(commentMapper);
        verifyNoInteractions(commentRepository);
    }

    @Test
    @DisplayName("댓글 삭제 성공 테스트 - 작성자 본인 확인")
    void testDeleteComment_Success() {
        // given
        Long commentId = 1L;

        // 댓글 작성자 설정 (ID: 1L)
        User commentWriter = mock(User.class);
        when(commentWriter.getId()).thenReturn(1L);

        PraiseComment comment = PraiseComment.builder()
                .id(commentId)
                .commentWriter(commentWriter) // 댓글의 작성자를 commentWriter로 설정
                .build();

        // mock 설정
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        // when
        // 삭제 요청자와 댓글 작성자가 동일한 사용자(ID: 1L)이므로 삭제 성공해야 함
        commentService.deleteComment(commentId, commentWriter);

        // then
        // 메서드 호출 검증
        verify(commentRepository).findById(commentId);
        verify(commentEmojiReactionRepository).deleteByComment(comment);
        verify(commentRepository).delete(comment);
    }

    @Test
    @DisplayName("댓글 삭제 실패 - 댓글 ID 없음")
    void testDeleteComment_CommentNotFound() {
        // given
        Long invalidCommentId = 999L;
        User commentWriter = mock(User.class);

        // mock 설정
        when(commentRepository.findById(invalidCommentId)).thenReturn(Optional.empty());

        // then
        assertThrows(CommentNotFoundException.class, () -> {
            // when
            commentService.deleteComment(invalidCommentId, commentWriter);
        });

        // 메서드 호출 검증
        verify(commentRepository).findById(invalidCommentId);
        verifyNoInteractions(commentEmojiReactionRepository);
    }

    @Test
    @DisplayName("댓글 삭제 실패 - 권한 없음")
    void testDeleteComment_ForbiddenAccess() {
        // given
        Long commentId = 1L;
        User unauthorizedUser = mock(User.class);
        when(unauthorizedUser.getId()).thenReturn(2L); // 다른 사용자 ID

        User commentWriter = mock(User.class);
        when(commentWriter.getId()).thenReturn(1L); // 원래 작성자 ID

        PraiseComment comment = PraiseComment.builder()
                .id(commentId)
                .commentWriter(commentWriter)
                .build();

        // mock 설정
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        // then
        assertThrows(ForbiddenCommentAccessException.class, () -> {
            // when
            commentService.deleteComment(commentId, unauthorizedUser);
        });

        // 메서드 호출 검증
        verify(commentRepository).findById(commentId);
        verifyNoInteractions(commentEmojiReactionRepository);
    }

    @Test
    @DisplayName("댓글 조회 성공 테스트")
    void testGetCommentsByPraise_Success() {
        // given
        Praise praise = Praise.builder()
                .id(1L)
                .build();

        List<PraiseComment> commentList = new ArrayList<>();
        commentList.add(PraiseComment.builder().id(1L).build());
        commentList.add(PraiseComment.builder().id(2L).build());

        // mock 설정
        when(commentRepository.findByPraise(praise)).thenReturn(commentList);

        // when
        List<PraiseComment> result = commentService.getCommentsByPraise(praise);

        // then
        assertNotNull(result, "댓글 목록은 null이 아니어야 합니다");
        assertEquals(2, result.size(), "댓글 목록 크기가 일치해야 합니다");

        // 메서드 호출 검증
        verify(commentRepository).findByPraise(praise);
    }
}
