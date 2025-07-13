package org.example.hugmeexp.domain.praise.service;

import org.example.hugmeexp.domain.notification.service.NotificationService;
import org.example.hugmeexp.domain.praise.dto.*;
import org.example.hugmeexp.domain.praise.entity.Praise;
import org.example.hugmeexp.domain.praise.entity.PraiseComment;
import org.example.hugmeexp.domain.praise.entity.PraiseEmojiReaction;
import org.example.hugmeexp.domain.praise.entity.PraiseReceiver;
import org.example.hugmeexp.domain.praise.enums.PraiseType;
import org.example.hugmeexp.domain.praise.exception.PraiseNotFoundException;
import org.example.hugmeexp.domain.praise.mapper.PraiseMapper;
import org.example.hugmeexp.domain.praise.repository.*;
import org.example.hugmeexp.domain.user.entity.User;
import org.example.hugmeexp.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PraiseServiceTest {

    @Mock
    private PraiseMapper praiseMapper;

    @Mock
    private PraiseRepository praiseRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PraiseEmojiReactionRepository praiseEmojiReactionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PraiseReceiverRepository praiseReceiverRepository;

    @Mock
    private CommentEmojiReactionRepository commentEmojiReactionRepository;

    @Mock
    private CommentService commentService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private PraiseService praiseService;

    private User sender;
    private User receiver1;
    private User receiver2;

    @BeforeEach
    void setUp() {
        // 테스트 유저 설정
        sender = createTestUser("sender", "보내는사람", "010-1111-2222", 1L);
        receiver1 = createTestUser("receiver1", "받는사람1", "010-3333-4444", 2L);
        receiver2 = createTestUser("receiver2", "받는사람2", "010-5555-6666", 3L);
    }

    // 테스트 유저 생성 헬퍼 메소드
    private User createTestUser(String username, String name, String phoneNumber, Long id) {
        User user = User.builder()
                .username(username)
                .password("password123")
                .phoneNumber(phoneNumber)
                .name(name)
                .build();
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    // 칭찬 생성 헬퍼 메소드
    private Praise createTestPraise(Long id, User sender, String content, PraiseType praiseType, LocalDateTime createdAt) {
        Praise praise = Praise.builder()
                .id(id)
                .sender(sender)
                .content(content)
                .praiseType(praiseType)
                .build();
        if (createdAt != null) {
            ReflectionTestUtils.setField(praise, "createdAt", createdAt);
        }
        return praise;
    }

    // 칭찬 수신자 생성 헬퍼 메소드
    private PraiseReceiver createTestPraiseReceiver(Long id, Praise praise, User receiver) {
        PraiseReceiver praiseReceiver = PraiseReceiver.builder()
                .id(id)
                .praise(praise)
                .receiver(receiver)
                .build();
        return praiseReceiver;
    }

    // 댓글 생성 헬퍼 메소드
    private PraiseComment createTestComment(Long id, Praise praise, User commentWriter, String content) {
        PraiseComment comment = PraiseComment.builder()
                .id(id)
                .praise(praise)
                .commentWriter(commentWriter)
                .content(content)
                .build();
        return comment;
    }

    @Test
    @DisplayName("칭찬 생성 테스트")
    void testCreatePraise() {
        // given
        PraiseRequestDTO requestDTO = createPraiseRequestDTO();
        Praise praise = createTestPraise(1L, sender, requestDTO.getContent(), requestDTO.getType(), null);

        // mock 설정
        setupMocksForCreatePraise(requestDTO, praise);

        // when
        PraiseResponseDTO result = praiseService.createPraise(requestDTO, sender);

        // then
        assertEquals(praise.getId(), result.getId());
        assertEquals(praise.getContent(), result.getContent());
        assertEquals(praise.getPraiseType(), result.getType());
        assertEquals(0, result.getCommentCount());
        // emojis 필드가 null이 아닌지 확인
        assertNotNull(result.getEmojis(), "emojis 필드는 null이 아니어야 합니다");
    }

    @Test
    @DisplayName("칭찬 생성 시 emojis 필드 빈 리스트 테스트")
    void testCreatePraiseEmojisEmptyList() {
        // given
        PraiseRequestDTO requestDTO = createPraiseRequestDTO();
        Praise praise = createTestPraise(1L, sender, requestDTO.getContent(), requestDTO.getType(), null);

        // mock 설정
        setupMocksForCreatePraise(requestDTO, praise);

        // when
        PraiseResponseDTO result = praiseService.createPraise(requestDTO, sender);

        // then
        // emojis 필드가 null이 아니고 빈 리스트여야 함
        assertNotNull(result.getEmojis(), "emojis 필드는 null이 아니어야 합니다");
        assertTrue(result.getEmojis().isEmpty(), "emojis 필드는 빈 리스트여야 합니다");
    }

    // 칭찬 요청 DTO 생성 헬퍼 메소드
    private PraiseRequestDTO createPraiseRequestDTO() {
        return PraiseRequestDTO.builder()
                .content("칭찬 내용")
                .type(PraiseType.THANKS)
                .receiverUsername(List.of(receiver1.getUsername(), receiver2.getUsername()))
                .build();
    }

    // 칭찬 생성 테스트를 위한 mock 설정 헬퍼 메소드
    private void setupMocksForCreatePraise(PraiseRequestDTO requestDTO, Praise praise) {
        when(praiseMapper.toEntity(requestDTO, sender)).thenReturn(praise);
        when(praiseRepository.save(praise)).thenReturn(praise);
        when(userRepository.findByUsername(receiver1.getUsername())).thenReturn(Optional.of(receiver1));
        when(userRepository.findByUsername(receiver2.getUsername())).thenReturn(Optional.of(receiver2));
    }

    @Test
    @DisplayName("날짜 범위로 칭찬 조회 테스트")
    void testFindByDateRange() {
        // given
        // 테스트 유저 설정
        User currentUser = createTestUser("currentUser", "테스트유저", "010-1111-2222", 3L);
        User otherUser1 = createTestUser("otherUser1", "다른유저1", "010-3333-4444", 4L);
        User otherUser2 = createTestUser("otherUser2", "다른유저2", "010-5555-6666", 5L);

        // 테스트 데이터 설정
        TestDataForDateRange testData = setupTestDataForDateRange(currentUser, otherUser1, otherUser2);

        // 6월 날짜 범위 설정
        LocalDate startDate = LocalDate.of(2025, 6, 1);
        LocalDate endDate = LocalDate.of(2025, 6, 30);

        // mock 설정
        setupMocksForFindByDateRange(startDate, endDate, testData);

        // when
        List<PraiseResponseDTO> result = praiseService.findByDateRange(startDate, endDate, currentUser, false);

        // then
        assertEquals(4, result.size(), "6월에 생성된 칭찬은 4개여야 합니다");

        // 결과에 포함된 칭찬 ID 확인
        List<Long> resultIds = result.stream().map(PraiseResponseDTO::getId).toList();
        assertTrue(resultIds.contains(testData.junePraises.get(0).getId()), "6월 칭찬1이 결과에 포함되어야 합니다");
        assertTrue(resultIds.contains(testData.junePraises.get(1).getId()), "6월 칭찬2가 결과에 포함되어야 합니다");
        assertTrue(resultIds.contains(testData.junePraises.get(2).getId()), "6월 칭찬3이 결과에 포함되어야 합니다");
        assertTrue(resultIds.contains(testData.junePraises.get(3).getId()), "6월 칭찬4가 결과에 포함되어야 합니다");

        // 댓글 수 확인
        for (PraiseResponseDTO dto : result) {
            assertEquals(1L, dto.getCommentCount(), "각 칭찬에는 댓글이 1개씩 있어야 합니다");
        }
    }



    // 날짜 범위 테스트를 위한 데이터 클래스
    private static class TestDataForDateRange {
        List<Praise> junePraises = new ArrayList<>();
        List<Praise> julyPraises = new ArrayList<>();
        List<PraiseReceiver> juneReceivers = new ArrayList<>();
        List<PraiseReceiver> julyReceivers = new ArrayList<>();
        List<PraiseComment> juneComments = new ArrayList<>();
        List<PraiseComment> julyComments = new ArrayList<>();
    }

    // 날짜 범위 테스트를 위한 테스트 데이터 설정
    private TestDataForDateRange setupTestDataForDateRange(User currentUser, User otherUser1, User otherUser2) {
        TestDataForDateRange testData = new TestDataForDateRange();

        // 7월 칭찬 2개 생성
        Praise julyPraise1 = createTestPraise(1L, currentUser, "7월에 보낸 칭찬", PraiseType.THANKS, 
                LocalDateTime.of(2025, 7, 15, 12, 0));
        Praise julyPraise2 = createTestPraise(2L, otherUser1, "7월에 다른 사람이 보낸 칭찬", PraiseType.THANKS, 
                LocalDateTime.of(2025, 7, 20, 12, 0));
        testData.julyPraises.add(julyPraise1);
        testData.julyPraises.add(julyPraise2);

        // 6월 칭찬 4개 생성
        Praise junePraise1 = createTestPraise(3L, currentUser, "6월에 내가 보낸 칭찬", PraiseType.THANKS, 
                LocalDateTime.of(2025, 6, 5, 12, 0));
        Praise junePraise2 = createTestPraise(4L, otherUser1, "6월에 다른 사람이 보낸 칭찬", PraiseType.THANKS, 
                LocalDateTime.of(2025, 6, 10, 12, 0));
        Praise junePraise3 = createTestPraise(5L, otherUser1, "6월에 다른 사람이 보낸 칭찬 2", PraiseType.THANKS, 
                LocalDateTime.of(2025, 6, 15, 12, 0));
        Praise junePraise4 = createTestPraise(6L, otherUser2, "6월에 다른 사람이 보낸 칭찬 3", PraiseType.THANKS, 
                LocalDateTime.of(2025, 6, 20, 12, 0));
        testData.junePraises.add(junePraise1);
        testData.junePraises.add(junePraise2);
        testData.junePraises.add(junePraise3);
        testData.junePraises.add(junePraise4);

        // PraiseReceiver 설정
        PraiseReceiver juneReceiver1 = createTestPraiseReceiver(1L, junePraise1, otherUser1);
        PraiseReceiver juneReceiver2 = createTestPraiseReceiver(2L, junePraise2, currentUser);
        PraiseReceiver juneReceiver3 = createTestPraiseReceiver(3L, junePraise3, otherUser2);
        PraiseReceiver juneReceiver4 = createTestPraiseReceiver(4L, junePraise4, otherUser1);
        testData.juneReceivers.add(juneReceiver1);
        testData.juneReceivers.add(juneReceiver2);
        testData.juneReceivers.add(juneReceiver3);
        testData.juneReceivers.add(juneReceiver4);

        PraiseReceiver julyReceiver1 = createTestPraiseReceiver(5L, julyPraise1, otherUser2);
        PraiseReceiver julyReceiver2 = createTestPraiseReceiver(6L, julyPraise2, otherUser1);
        testData.julyReceivers.add(julyReceiver1);
        testData.julyReceivers.add(julyReceiver2);

        // 댓글 설정
        PraiseComment juneComment1 = createTestComment(1L, junePraise1, otherUser1, "6월 칭찬1에 대한 댓글");
        PraiseComment juneComment2 = createTestComment(2L, junePraise2, currentUser, "6월 칭찬2에 대한 댓글");
        PraiseComment juneComment3 = createTestComment(3L, junePraise3, otherUser2, "6월 칭찬3에 대한 댓글");
        PraiseComment juneComment4 = createTestComment(4L, junePraise4, otherUser1, "6월 칭찬4에 대한 댓글");
        testData.juneComments.add(juneComment1);
        testData.juneComments.add(juneComment2);
        testData.juneComments.add(juneComment3);
        testData.juneComments.add(juneComment4);

        return testData;
    }

    // 날짜 범위 테스트를 위한 mock 설정
    private void setupMocksForFindByDateRange(LocalDate startDate, LocalDate endDate, TestDataForDateRange testData) {
        // mock 설정
        when(praiseRepository.findByCreatedAtBetween(
                eq(startDate.atStartOfDay()),
                eq(endDate.atTime(LocalTime.MAX))))
                .thenReturn(testData.junePraises);

        // 칭찬 받는 사람 리스트 매핑
        when(praiseReceiverRepository.findByPraiseIn(anyList()))
                .thenReturn(testData.juneReceivers);

        // 댓글 수 조회
        for (Praise praise : testData.junePraises) {
            when(commentRepository.countByPraise(praise)).thenReturn(1L);
        }

        // 이모지 반응 조회 (이 테스트에서는 필요 없음)
        when(praiseEmojiReactionRepository.findByPraise(any(Praise.class)))
                .thenReturn(List.of());

        // 댓글 조회
        for (int i = 0; i < testData.junePraises.size(); i++) {
            when(commentService.getCommentsByPraise(testData.junePraises.get(i)))
                    .thenReturn(List.of(testData.juneComments.get(i)));
        }
    }

    @Test
    @DisplayName("날짜 범위로 나와 관련된 칭찬만 조회 테스트")
    void testFindByDateRange_OnlyRelatedToMe() {
        // given
        // 테스트 유저 설정
        User currentUser = createTestUser("currentUser", "테스트유저", "010-1111-2222", 3L);
        User otherUser1 = createTestUser("otherUser1", "다른유저1", "010-3333-4444", 4L);
        User otherUser2 = createTestUser("otherUser2", "다른유저2", "010-5555-6666", 5L);

        // 테스트 데이터 설정
        TestDataForDateRange testData = setupTestDataForDateRange(currentUser, otherUser1, otherUser2);

        // 6월 날짜 범위 설정
        LocalDate startDate = LocalDate.of(2025, 6, 1);
        LocalDate endDate = LocalDate.of(2025, 6, 30);

        // mock 설정 - 나와 관련된 칭찬만 조회
        setupMocksForFindByDateRangeOnlyRelatedToMe(startDate, endDate, currentUser, testData);

        // when
        List<PraiseResponseDTO> result = praiseService.findByDateRange(startDate, endDate, currentUser, true);

        // then
        assertEquals(2, result.size(), "6월에 나와 관련된 칭찬은 2개여야 합니다");

        // 결과에 포함된 칭찬 ID 확인
        List<Long> resultIds = result.stream().map(PraiseResponseDTO::getId).toList();
        assertTrue(resultIds.contains(testData.junePraises.get(0).getId()), "내가 보낸 칭찬이 결과에 포함되어야 합니다");
        assertTrue(resultIds.contains(testData.junePraises.get(1).getId()), "내가 받은 칭찬이 결과에 포함되어야 합니다");

        // 댓글 수 확인
        for (PraiseResponseDTO dto : result) {
            assertEquals(1L, dto.getCommentCount(), "각 칭찬에는 댓글이 1개씩 있어야 합니다");
        }
    }

    // 나와 관련된 칭찬만 조회하는 테스트를 위한 mock 설정
    private void setupMocksForFindByDateRangeOnlyRelatedToMe(LocalDate startDate, LocalDate endDate, 
                                                           User currentUser, TestDataForDateRange testData) {
        // 내가 받은 칭찬 (6월)
        when(praiseReceiverRepository.findByReceiverAndCreatedAtBetween(
                eq(currentUser),
                eq(startDate.atStartOfDay()),
                eq(endDate.atTime(LocalTime.MAX))))
                .thenReturn(List.of(testData.juneReceivers.get(1))); // juneReceiver1 (내가 받은 칭찬)

        // 내가 보낸 칭찬 (6월)
        when(praiseRepository.findBySenderAndCreatedAtBetween(
                eq(currentUser),
                eq(startDate.atStartOfDay()),
                eq(endDate.atTime(LocalTime.MAX))))
                .thenReturn(List.of(testData.junePraises.get(0))); // junePraise1 (내가 보낸 칭찬)

        // 칭찬 받는 사람 리스트 매핑
        when(praiseReceiverRepository.findByPraiseIn(anyList()))
                .thenReturn(List.of(testData.juneReceivers.get(0), testData.juneReceivers.get(1))); // 내가 보낸 칭찬의 수신자, 내가 받은 칭찬

        // 댓글 수 조회
        when(commentRepository.countByPraise(testData.junePraises.get(0))).thenReturn(1L);
        when(commentRepository.countByPraise(testData.junePraises.get(1))).thenReturn(1L);

        // 이모지 반응 조회 (이 테스트에서는 필요 없음)
        when(praiseEmojiReactionRepository.findByPraise(any(Praise.class)))
                .thenReturn(List.of());

        // 댓글 조회
        when(commentService.getCommentsByPraise(testData.junePraises.get(0))).thenReturn(List.of(testData.juneComments.get(0)));
        when(commentService.getCommentsByPraise(testData.junePraises.get(1))).thenReturn(List.of(testData.juneComments.get(1)));
    }

    @Test
    @DisplayName("날짜 범위로 나와 관련된 칭찬 조회 실패 - 데이터 없음")
    void testFindByDateRange_OnlyRelatedToMe_NoData() {
        // given
        User currentUser = createTestUser("currentUser", "테스트유저", "010-1111-2222", 3L);
        LocalDate startDate = LocalDate.of(2025, 6, 1);
        LocalDate endDate = LocalDate.of(2025, 6, 30);

        // mock 설정 - 데이터 없음
        when(praiseReceiverRepository.findByReceiverAndCreatedAtBetween(
                eq(currentUser),
                eq(startDate.atStartOfDay()),
                eq(endDate.atTime(LocalTime.MAX))))
                .thenReturn(List.of()); // 빈 리스트 반환

        when(praiseRepository.findBySenderAndCreatedAtBetween(
                eq(currentUser),
                eq(startDate.atStartOfDay()),
                eq(endDate.atTime(LocalTime.MAX))))
                .thenReturn(List.of()); // 빈 리스트 반환

        // when
        List<PraiseResponseDTO> result = praiseService.findByDateRange(startDate, endDate, currentUser, true);

        // then
        assertTrue(result.isEmpty(), "결과 리스트는 비어 있어야 합니다");
    }

    @Test
    @DisplayName("날짜 범위로 나와 관련된 칭찬 조회 실패 - 예외 발생")
    void testFindByDateRange_OnlyRelatedToMe_Exception() {
        // given
        User currentUser = createTestUser("currentUser", "테스트유저", "010-1111-2222", 3L);
        LocalDate startDate = LocalDate.of(2025, 6, 1);
        LocalDate endDate = LocalDate.of(2025, 6, 30);

        // mock 설정 - 예외 발생
        when(praiseReceiverRepository.findByReceiverAndCreatedAtBetween(
                eq(currentUser),
                eq(startDate.atStartOfDay()),
                eq(endDate.atTime(LocalTime.MAX))))
                .thenThrow(new RuntimeException("데이터베이스 오류"));

        // then
        assertThrows(RuntimeException.class, () -> {
            // when
            praiseService.findByDateRange(startDate, endDate, currentUser, true);
        }, "예외가 발생해야 합니다");
    }

    @Test
    @DisplayName("날짜 범위로 나와 관련된 칭찬 조회 실패 - null 반환")
    void testFindByDateRange_OnlyRelatedToMe_NullData() {
        // given
        User currentUser = createTestUser("currentUser", "테스트유저", "010-1111-2222", 3L);
        LocalDate startDate = LocalDate.of(2025, 6, 1);
        LocalDate endDate = LocalDate.of(2025, 6, 30);

        // mock 설정 - null 반환
        when(praiseReceiverRepository.findByReceiverAndCreatedAtBetween(
                eq(currentUser),
                eq(startDate.atStartOfDay()),
                eq(endDate.atTime(LocalTime.MAX))))
                .thenReturn(null); // null 반환

        // then
        assertThrows(NullPointerException.class, () -> {
            // when
            praiseService.findByDateRange(startDate, endDate, currentUser, true);
        }, "NullPointerException이 발생해야 합니다");
    }

    @Test
    @DisplayName("키워드와 날짜로 나와 관련된 칭찬 조회 테스트")
    void testSearchByKeywordAndDate_OnlyRelatedToMe() {
        // given
        // 테스트 유저 설정
        User currentUser = createTestUser("currentUser", "테스트유저", "010-1111-2222", 3L);
        User otherUser1 = createTestUser("otherUser1", "다른유저1", "010-3333-4444", 4L);
        User otherUser2 = createTestUser("otherUser2", "다른유저2", "010-5555-6666", 5L);

        // 테스트 데이터 설정
        TestDataForDateRange testData = setupTestDataForDateRange(currentUser, otherUser1, otherUser2);

        // 6월 날짜 범위 설정
        LocalDate startDate = LocalDate.of(2025, 6, 1);
        LocalDate endDate = LocalDate.of(2025, 6, 30);

        // 검색 키워드 설정 (현재 유저의 이름)
        String keyword = "테스트유저";

        // mock 설정 - 키워드로 나와 관련된 칭찬만 조회
        setupMocksForSearchByKeywordAndDate(startDate, endDate, currentUser, keyword, testData);

        // when
        List<PraiseResponseDTO> result = praiseService.searchByKeywordAndDate(startDate, endDate, currentUser, true, keyword);

        // then
        assertEquals(2, result.size(), "키워드 '테스트유저'로 검색한 나와 관련된 칭찬은 2개여야 합니다");

        // 결과에 포함된 칭찬 ID 확인
        List<Long> resultIds = result.stream().map(PraiseResponseDTO::getId).toList();
        assertTrue(resultIds.contains(testData.junePraises.get(0).getId()), "내가 보낸 칭찬이 결과에 포함되어야 합니다");
        assertTrue(resultIds.contains(testData.junePraises.get(1).getId()), "내가 받은 칭찬이 결과에 포함되어야 합니다");

        // 댓글 수 확인
        for (PraiseResponseDTO dto : result) {
            assertEquals(1L, dto.getCommentCount(), "각 칭찬에는 댓글이 1개씩 있어야 합니다");
        }
    }

    // 키워드로 나와 관련된 칭찬 조회 테스트를 위한 mock 설정
    private void setupMocksForSearchByKeywordAndDate(LocalDate startDate, LocalDate endDate, 
                                                   User currentUser, String keyword, TestDataForDateRange testData) {
        // 내가 받은 칭찬 중에서, 보낸 사람 이름 또는 받은 사람에 keyword가 포함된 칭찬 조회
        when(praiseReceiverRepository.findRelatedPraiseByReceiverWithKeyword(
                eq(currentUser),
                eq(startDate.atStartOfDay()),
                eq(endDate.atTime(LocalTime.MAX)),
                eq(keyword)))
                .thenReturn(List.of(testData.juneReceivers.get(1))); // 내가 받은 칭찬

        // 내가 보낸 칭찬 중에서, 보낸 사람 이름 또는 받은 사람에 keyword가 포함된 것 조회
        when(praiseRepository.findMySentPraiseWithKeyword(
                eq(currentUser),
                eq(startDate.atStartOfDay()),
                eq(endDate.atTime(LocalTime.MAX)),
                eq(keyword)))
                .thenReturn(List.of(testData.junePraises.get(0))); // 내가 보낸 칭찬

        // 칭찬 받는 사람 리스트 매핑
        when(praiseReceiverRepository.findByPraiseIn(anyList()))
                .thenReturn(List.of(testData.juneReceivers.get(0), testData.juneReceivers.get(1))); // 내가 보낸 칭찬의 수신자, 내가 받은 칭찬

        // 댓글 수 조회
        when(commentRepository.countByPraise(testData.junePraises.get(0))).thenReturn(1L);
        when(commentRepository.countByPraise(testData.junePraises.get(1))).thenReturn(1L);

        // 이모지 반응 조회 (이 테스트에서는 필요 없음)
        when(praiseEmojiReactionRepository.findByPraise(any(Praise.class)))
                .thenReturn(List.of());

        // 댓글 조회
        when(commentService.getCommentsByPraise(testData.junePraises.get(0))).thenReturn(List.of(testData.juneComments.get(0)));
        when(commentService.getCommentsByPraise(testData.junePraises.get(1))).thenReturn(List.of(testData.juneComments.get(1)));
    }

    @Test
    @DisplayName("키워드와 날짜로 나와 관련된 칭찬 조회 실패 - 데이터 없음")
    void testSearchByKeywordAndDate_OnlyRelatedToMe_NoData() {
        // given
        User currentUser = createTestUser("currentUser", "테스트유저", "010-1111-2222", 3L);
        LocalDate startDate = LocalDate.of(2025, 6, 1);
        LocalDate endDate = LocalDate.of(2025, 6, 30);
        String keyword = "테스트유저";

        // mock 설정 - 데이터 없음
        when(praiseReceiverRepository.findRelatedPraiseByReceiverWithKeyword(
                eq(currentUser),
                eq(startDate.atStartOfDay()),
                eq(endDate.atTime(LocalTime.MAX)),
                eq(keyword)))
                .thenReturn(List.of()); // 빈 리스트 반환

        when(praiseRepository.findMySentPraiseWithKeyword(
                eq(currentUser),
                eq(startDate.atStartOfDay()),
                eq(endDate.atTime(LocalTime.MAX)),
                eq(keyword)))
                .thenReturn(List.of()); // 빈 리스트 반환

        // when
        List<PraiseResponseDTO> result = praiseService.searchByKeywordAndDate(startDate, endDate, currentUser, true, keyword);

        // then
        assertTrue(result.isEmpty(), "결과 리스트는 비어 있어야 합니다");
    }

    @Test
    @DisplayName("키워드와 날짜로 나와 관련된 칭찬 조회 실패 - 예외 발생")
    void testSearchByKeywordAndDate_OnlyRelatedToMe_Exception() {
        // given
        User currentUser = createTestUser("currentUser", "테스트유저", "010-1111-2222", 3L);
        LocalDate startDate = LocalDate.of(2025, 6, 1);
        LocalDate endDate = LocalDate.of(2025, 6, 30);
        String keyword = "테스트유저";

        // mock 설정 - 예외 발생
        when(praiseReceiverRepository.findRelatedPraiseByReceiverWithKeyword(
                eq(currentUser),
                eq(startDate.atStartOfDay()),
                eq(endDate.atTime(LocalTime.MAX)),
                eq(keyword)))
                .thenThrow(new RuntimeException("데이터베이스 오류"));

        // then
        assertThrows(RuntimeException.class, () -> {
            // when
            praiseService.searchByKeywordAndDate(startDate, endDate, currentUser, true, keyword);
        }, "예외가 발생해야 합니다");
    }

    @Test
    @DisplayName("키워드와 날짜로 나와 관련된 칭찬 조회 실패 - null 반환")
    void testSearchByKeywordAndDate_OnlyRelatedToMe_NullData() {
        // given
        User currentUser = createTestUser("currentUser", "테스트유저", "010-1111-2222", 3L);
        LocalDate startDate = LocalDate.of(2025, 6, 1);
        LocalDate endDate = LocalDate.of(2025, 6, 30);
        String keyword = "테스트유저";

        // mock 설정 - null 반환
        when(praiseReceiverRepository.findRelatedPraiseByReceiverWithKeyword(
                eq(currentUser),
                eq(startDate.atStartOfDay()),
                eq(endDate.atTime(LocalTime.MAX)),
                eq(keyword)))
                .thenReturn(null); // null 반환

        // then
        assertThrows(NullPointerException.class, () -> {
            // when
            praiseService.searchByKeywordAndDate(startDate, endDate, currentUser, true, keyword);
        }, "NullPointerException이 발생해야 합니다");
    }

    @Test
    @DisplayName("칭찬 상세 조회 성공")
    void testGetPraiseDetail_Success() {
        // given
        Long praiseId = 1L;

        // 테스트 유저 설정
        User sender = createTestUser("sender", "보낸사람", "01012345678", 1L);
        User receiver = createTestUser("receiver", "받은사람", "01012345679", 2L);
        User commentWriter = createTestUser("commenter", "댓글작성자", "01012345670", 3L);

        // 칭찬 엔티티 설정
        Praise praise = createTestPraise(praiseId, sender, "칭찬 내용", PraiseType.THANKS, 
                LocalDateTime.of(2023, 1, 1, 12, 0));

        // 칭찬 받은 사람 설정
        PraiseReceiver praiseReceiver = createTestPraiseReceiver(1L, praise, receiver);

        // 댓글 설정
        PraiseComment comment = createTestComment(1L, praise, commentWriter, "댓글 내용");

        // 이모지 반응 설정
        org.example.hugmeexp.domain.praise.entity.PraiseEmojiReaction reaction = 
            createPraiseEmojiReaction(1L, praise, sender, "👍");

        // 댓글 이모지 반응 설정
        org.example.hugmeexp.domain.praise.entity.CommentEmojiReaction commentReaction = 
            createCommentEmojiReaction(1L, comment, sender, "❤️");

        // mock 설정
        setupMocksForGetPraiseDetail(praiseId, praise, praiseReceiver, comment, reaction, commentReaction);

        // when
        PraiseDetailResponseDTO result = praiseService.getPraiseDetail(praiseId);

        // then
        assertEquals(praiseId, result.getId());
        assertEquals(sender.getName(), result.getSenderName());
        assertEquals(praise.getContent(), result.getContent());
        assertEquals(praise.getPraiseType(), result.getType());
        assertEquals(praise.getCreatedAt(), result.getCreatedAt());

        // 받은 사람 확인
        assertEquals(1, result.getReceivers().size());
        assertEquals(receiver.getName(), result.getReceivers().get(0).getName());

        // 댓글 확인
        assertEquals(1, result.getCommentCount());
        assertEquals(1, result.getComments().size());
        assertEquals(comment.getId(), result.getComments().get(0).getId());
        assertEquals(comment.getContent(), result.getComments().get(0).getContent());

        // 이모지 반응 확인
        assertEquals(1, result.getEmojiReactions().size());
        assertEquals("👍", result.getEmojiReactions().get(0).getEmoji());
        assertEquals(1, result.getEmojiReactions().get(0).getCount());
    }

    // 칭찬 이모지 반응 생성 헬퍼 메소드
    private org.example.hugmeexp.domain.praise.entity.PraiseEmojiReaction createPraiseEmojiReaction(
            Long id, Praise praise, User reactorWriter, String emoji) {
        org.example.hugmeexp.domain.praise.entity.PraiseEmojiReaction reaction = 
            org.example.hugmeexp.domain.praise.entity.PraiseEmojiReaction.builder()
                .praise(praise)
                .reactorWriter(reactorWriter)
                .emoji(emoji)
                .build();
        ReflectionTestUtils.setField(reaction, "id", id);
        return reaction;
    }

    // 댓글 이모지 반응 생성 헬퍼 메소드
    private org.example.hugmeexp.domain.praise.entity.CommentEmojiReaction createCommentEmojiReaction(
            Long id, PraiseComment comment, User reactorWriter, String emoji) {
        org.example.hugmeexp.domain.praise.entity.CommentEmojiReaction reaction = 
            org.example.hugmeexp.domain.praise.entity.CommentEmojiReaction.builder()
                .comment(comment)
                .reactorWriter(reactorWriter)
                .emoji(emoji)
                .build();
        ReflectionTestUtils.setField(reaction, "id", id);
        return reaction;
    }

    // 칭찬 상세 조회 테스트를 위한 mock 설정
    private void setupMocksForGetPraiseDetail(Long praiseId, Praise praise, PraiseReceiver praiseReceiver, 
                                            PraiseComment comment, 
                                            org.example.hugmeexp.domain.praise.entity.PraiseEmojiReaction reaction,
                                            org.example.hugmeexp.domain.praise.entity.CommentEmojiReaction commentReaction) {
        when(praiseRepository.findWithSenderById(praiseId)).thenReturn(Optional.of(praise));
        when(praiseReceiverRepository.findByPraise(praise)).thenReturn(List.of(praiseReceiver));
        when(commentService.getCommentsByPraise(praise)).thenReturn(List.of(comment));
        when(praiseEmojiReactionRepository.findByPraise(praise)).thenReturn(List.of(reaction));
        when(commentEmojiReactionRepository.findWithReactorByPraise(praise)).thenReturn(List.of(commentReaction));
    }

    @Test
    @DisplayName("칭찬 상세 조회 실패 - 존재하지 않는 ID")
    void testGetPraiseDetail_NotFound() {
        // given
        Long invalidId = 999L;
        when(praiseRepository.findWithSenderById(invalidId)).thenReturn(Optional.empty());

        // then
        assertThrows(PraiseNotFoundException.class, () -> praiseService.getPraiseDetail(invalidId));
    }

    @Test
    @DisplayName("칭찬 상세 조회 실패 - null 반환")
    void testGetPraiseDetail_NullData() {
        // given
        Long praiseId = 1L;

        Praise dummyPraise = createTestPraise(praiseId, sender, "내용", PraiseType.THANKS, LocalDateTime.now());

        when(praiseRepository.findWithSenderById(praiseId)).thenReturn(Optional.of(dummyPraise));
        when(praiseReceiverRepository.findByPraise(dummyPraise)).thenReturn(List.of());
        when(commentService.getCommentsByPraise(dummyPraise)).thenReturn(null);

        // then
        assertThrows(NullPointerException.class, () -> {
            // when
            praiseService.getPraiseDetail(praiseId);
        }, "NullPointerException이 발생해야 합니다");
    }

    @Test
    @DisplayName("반응 수 기준으로 칭찬글 정렬 테스트")
    void testFindPopularPraises() {
        // given
        LocalDate startDate = LocalDate.of(2025, 6, 1);
        LocalDate endDate = LocalDate.of(2025, 6, 30);
        int limit = 3;

        // 테스트 데이터 설정
        Praise praise1 = createTestPraise(1L, sender, "칭찬1", PraiseType.THANKS, LocalDateTime.of(2025, 6, 5, 12, 0));
        Praise praise2 = createTestPraise(2L, sender, "칭찬2", PraiseType.THANKS, LocalDateTime.of(2025, 6, 10, 12, 0));
        Praise praise3 = createTestPraise(3L, sender, "칭찬3", PraiseType.THANKS, LocalDateTime.of(2025, 6, 15, 12, 0));
        Praise praise4 = createTestPraise(4L, sender, "칭찬4", PraiseType.THANKS, LocalDateTime.of(2025, 6, 20, 12, 0));

        List<Praise> praises = List.of(praise1, praise2, praise3, praise4);

        // 이모지 반응 설정
        PraiseEmojiReaction reaction1 = createPraiseEmojiReaction(1L, praise1, sender, "👍");
        PraiseEmojiReaction reaction2 = createPraiseEmojiReaction(2L, praise2, sender, "👍");
        PraiseEmojiReaction reaction3 = createPraiseEmojiReaction(3L, praise3, sender, "👍");
        PraiseEmojiReaction reaction4 = createPraiseEmojiReaction(4L, praise3, sender, "❤️");

        // mock 설정
        when(praiseRepository.findByCreatedAtBetween(
                eq(startDate.atStartOfDay()),
                eq(endDate.atTime(LocalTime.MAX))))
                .thenReturn(praises);

        when(praiseEmojiReactionRepository.findByPraise(praise1)).thenReturn(List.of(reaction1));
        when(praiseEmojiReactionRepository.findByPraise(praise2)).thenReturn(List.of(reaction2));
        when(praiseEmojiReactionRepository.findByPraise(praise3)).thenReturn(List.of(reaction3, reaction4));
        when(praiseEmojiReactionRepository.findByPraise(praise4)).thenReturn(List.of());

        // when
        List<PraiseResponseDTO> result = praiseService.findPopularPraises(startDate, endDate, limit);

        // then
        assertEquals(limit, result.size(), "결과는 상위 3개의 칭찬글이어야 합니다");
        assertEquals(praise3.getId(), result.get(0).getId(), "첫 번째 칭찬글은 반응 수가 가장 많은 칭찬글이어야 합니다");
        assertEquals(praise1.getId(), result.get(1).getId(), "두 번째 칭찬글은 반응 수가 두 번째로 많은 칭찬글이어야 합니다");
        assertEquals(praise2.getId(), result.get(2).getId(), "세 번째 칭찬글은 반응 수가 세 번째로 많은 칭찬글이어야 합니다");
    }

    @Test
    @DisplayName("반응 수 기준으로 칭찬글 정렬 실패 - 데이터 없음")
    void testFindPopularPraises_NoData() {
        // given
        LocalDate startDate = LocalDate.of(2025, 6, 1);
        LocalDate endDate = LocalDate.of(2025, 6, 30);
        int limit = 3;

        // mock 설정 - 데이터 없음
        when(praiseRepository.findByCreatedAtBetween(
                eq(startDate.atStartOfDay()),
                eq(endDate.atTime(LocalTime.MAX))))
                .thenReturn(List.of()); // 빈 리스트 반환

        // when
        List<PraiseResponseDTO> result = praiseService.findPopularPraises(startDate, endDate, limit);

        // then
        assertTrue(result.isEmpty(), "결과 리스트는 비어 있어야 합니다");
    }

    @Test
    @DisplayName("반응 수 기준으로 칭찬글 정렬 실패 - 예외 발생")
    void testFindPopularPraises_Exception() {
        // given
        LocalDate startDate = LocalDate.of(2025, 6, 1);
        LocalDate endDate = LocalDate.of(2025, 6, 30);
        int limit = 3;

        // mock 설정 - 예외 발생
        when(praiseRepository.findByCreatedAtBetween(
                eq(startDate.atStartOfDay()),
                eq(endDate.atTime(LocalTime.MAX))))
                .thenThrow(new RuntimeException("데이터베이스 오류"));

        // then
        assertThrows(RuntimeException.class, () -> {
            // when
            praiseService.findPopularPraises(startDate, endDate, limit);
        }, "예외가 발생해야 합니다");
    }

    @Test
    @DisplayName("반응 수 기준으로 칭찬글 정렬 실패 - null 반환")
    void testFindPopularPraises_NullData() {
        // given
        LocalDate startDate = LocalDate.of(2025, 6, 1);
        LocalDate endDate = LocalDate.of(2025, 6, 30);
        int limit = 3;

        // mock 설정 - null 반환
        when(praiseRepository.findByCreatedAtBetween(
                eq(startDate.atStartOfDay()),
                eq(endDate.atTime(LocalTime.MAX))))
                .thenReturn(null); // null 반환

        // then
        assertThrows(NullPointerException.class, () -> {
            // when
            praiseService.findPopularPraises(startDate, endDate, limit);
        }, "NullPointerException이 발생해야 합니다");
    }
}
