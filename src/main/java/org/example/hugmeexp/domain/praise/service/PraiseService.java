package org.example.hugmeexp.domain.praise.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.praise.dto.*;
import org.example.hugmeexp.domain.praise.entity.Praise;
import org.example.hugmeexp.domain.praise.entity.PraiseComment;
import org.example.hugmeexp.domain.praise.entity.PraiseReceiver;
import org.example.hugmeexp.domain.praise.enums.PraiseType;
import org.example.hugmeexp.domain.praise.exception.PraiseNotFoundException;
import org.example.hugmeexp.domain.praise.exception.UserNotFoundInPraiseException;
import org.example.hugmeexp.domain.praise.mapper.PraiseMapper;
import org.example.hugmeexp.domain.praise.repository.*;
import org.example.hugmeexp.domain.user.repository.UserRepository;
import org.example.hugmeexp.domain.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class PraiseService {

    private final PraiseMapper praiseMapper;
    private final PraiseRepository praiseRepository;
    private final CommentRepository commentRepository;
    private final PraiseEmojiReactionRepository praiseEmojiReactionRepository;
    private final UserRepository userRepository;
    private final PraiseReceiverRepository praiseReceiverRepository;
    private final CommentEmojiReactionRepository commentEmojiReactionRepository;
    private final CommentService commentService;


    /* 칭찬 생성 */
    @Transactional
    public PraiseResponseDTO createPraise(PraiseRequestDTO praiseRequestDTO, User sender) {

        try {
            List<User> receiverUsers = praiseRequestDTO.getReceiverUsername().stream()
                    .map(username -> userRepository.findByUsername(username)
                            .orElseThrow(UserNotFoundInPraiseException::new)).toList();

            // DTO -> Entity
             Praise praise = praiseMapper.toEntity(praiseRequestDTO, sender);

            // DB 에 저장
            Praise saved = praiseRepository.save(praise);

            // PraiseReceiver 저장
            List<PraiseReceiver> praiseReceivers = receiverUsers.stream()
                    .map(receiver -> PraiseReceiver.builder()
                            .praise(saved)
                            .receiver(receiver)
                            .build())
                    .toList();
            praiseReceiverRepository.saveAll(praiseReceivers);

            // Entity -> DTO
            return PraiseResponseDTO.from(saved, praiseReceivers, 0L, null);
        } catch (UserNotFoundInPraiseException e){

            throw e;
        }

    }

    /* 날짜 조회 + 나와 관련된 칭찬 조건 */
    public List<PraiseResponseDTO> findByDateRange(LocalDate startDate, LocalDate endDate, User currentUser, boolean me) {

        LocalDateTime startDateTime = startDate.atStartOfDay();    // 2025-06-01 00:00:00
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);    // 2025-06-18 23:59:59.999

        List<Praise> praiseList;

        if(me){
            // 내가 받은 칭찬
            List<PraiseReceiver> received = praiseReceiverRepository.findByReceiverAndCreatedAtBetween(currentUser,startDateTime,endDateTime);
            // 여러 명에게 칭찬 보냈을 경우 중복되어 보이는 칭찬 중복 제거
            List<Praise> receivedPraises = received.stream().map(PraiseReceiver::getPraise).distinct().toList();

            // 내가 보낸 칭찬
            List<Praise> sent = praiseRepository.findBySenderAndCreatedAtBetween(currentUser,startDateTime,endDateTime);

            // 둘을 합치고 중복 제거
            praiseList = Stream.concat(receivedPraises.stream(),sent.stream()).distinct().toList();

        }else {
            // 전체 칭찬 조회
            praiseList = praiseRepository.findByCreatedAtBetween(startDateTime, endDateTime);
        }

        // 칭찬 받는 사람 리스트 매핑
        Map<Long, List<PraiseReceiver>> receiverMap = praiseReceiverRepository.findByPraiseIn(praiseList).stream()
                .collect(Collectors.groupingBy(pr -> pr.getPraise().getId()));


        return praiseList.stream()
                .map(praise -> {
                    long commentCount = commentRepository.countByPraise(praise);
                    List<Object[]> counts = praiseEmojiReactionRepository.countGroupedByEmoji(praise);

                    Map<String, Integer> emojiCount = counts.stream()
                            .collect(Collectors.toMap(
                                    row -> (String) row[0],
                                    row -> ((Long) row[1]).intValue()
                            ));

                    List<PraiseReceiver> receivers = receiverMap.getOrDefault(praise.getId(),List.of());

                    return PraiseResponseDTO.from(praise,receivers,commentCount,emojiCount);

                }).collect(Collectors.toList());
    }

    /* 날짜 조회 + 나와 관련된 칭찬 조건 + keyword 조건 */
    public List<PraiseResponseDTO> searchByKeywordAndDate(LocalDate startDate, LocalDate endDate, User currentUser, boolean me, String keyword) {

        LocalDateTime startDateTime = startDate.atStartOfDay();    // 2025-06-01 00:00:00
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);    // 2025-06-18 23:59:59.999

        List<Praise> praiseList;

        if(me){
            // 내가 받은 칭찬 중에서, 보낸 사람 이름 또는 받은 사람에 keyword 가 포함된 칭찬 조회
            List<PraiseReceiver> received = praiseReceiverRepository.findRelatedPraiseByReceiverWithKeyword(currentUser, startDateTime,endDateTime,keyword);
            // 여러 명에게 칭찬 보냈을 경우 중복되어 보이는 칭찬 중복 제거
            List<Praise> receivedPraises = received.stream()
                    .map(PraiseReceiver::getPraise)
                    .distinct()
                    .toList();

            // 내가 보낸 칭찬 중에서, 보낸 사람 이름 또는 받은 사람에 keyword 가 포함된 것 조회
            List<Praise> sent = praiseRepository.findMySentPraiseWithKeyword(currentUser,startDateTime,endDateTime,keyword);

            // 둘을 합치고 중복 제거
            praiseList = Stream.concat(receivedPraises.stream(),sent.stream())
                    .distinct()
                    .toList();

        }else {
            // 전체 칭찬 조회
            praiseList = praiseRepository.findAllPraisesBySenderOrReceiverNameContaining(startDateTime, endDateTime, keyword);
        }

        // 칭찬 받는 사람 리스트 매핑
        Map<Long, List<PraiseReceiver>> receiverMap = praiseReceiverRepository.findByPraiseIn(praiseList).stream()
                .collect(Collectors.groupingBy(pr -> pr.getPraise().getId()));

        return praiseList.stream()
                .map(praise -> {
                    long commentCount = commentRepository.countByPraise(praise);

                    List<Object[]> counts = praiseEmojiReactionRepository.countGroupedByEmoji(praise);
                    Map<String, Integer> emojiCount = counts.stream()
                            .collect(Collectors.toMap(
                                    row -> (String) row[0],
                                    row -> ((Long) row[1]).intValue()
                            ));
                    List<PraiseReceiver> receivers = receiverMap.getOrDefault(praise.getId(),List.of());

                    return PraiseResponseDTO.from(praise,receivers,commentCount,emojiCount);
                }).collect(Collectors.toList());

    }


    /* 칭찬 반응 좋은 칭찬글 */
    public List<PraiseResponseDTO> findPopularPraises(LocalDate startDate, LocalDate endDate, int i) {

        LocalDateTime startDateTime = startDate.atStartOfDay();    // 2025-06-01 00:00:00
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);    // 2025-06-18 23:59:59.999

        // 해당 기간 내 칭찬글 전체 조회
        List<Praise> praiseList = praiseRepository.findByCreatedAtBetween(startDateTime, endDateTime);

        // 받는 사람들 매핑
        Map<Long, List<PraiseReceiver>> receiverMap = praiseReceiverRepository.findByPraiseIn(praiseList).stream()
                .collect(Collectors.groupingBy(praiseReceiver -> praiseReceiver.getPraise().getId()));

        // DTO 변환 + 이모지 반응 수 기준 정렬
        return praiseList.stream()
                .map(praise -> {
                    long commentCount = commentRepository.countByPraise(praise);

                    // 이모지 카운트 변환 처리
//                    Map<String, Integer> emojiCount = praiseEmojiReactionRepository.countGroupedMapByPraise(praise);
                    List<Object[]> counts = praiseEmojiReactionRepository.countGroupedByEmoji(praise);
                    Map<String, Integer> emojiCount = counts.stream()
                            .collect(Collectors.toMap(
                                    row -> (String) row[0],
                                    row -> ((Long) row[1]).intValue()
                            ));

                    // 수신자 리스트
                    List<PraiseReceiver> receivers = receiverMap.getOrDefault(praise.getId(), List.of());

                    return PraiseResponseDTO.from(praise,receivers,commentCount,emojiCount);

                })

                // 이모지 반응 수 총합 기준 내림차순 정렬
                .sorted(Comparator.comparingInt((PraiseResponseDTO p) ->
                        p.getEmojiReactionCount() == null ? 0 : p.getEmojiReactionCount().values().stream().mapToInt(Integer::intValue).sum()
                ).reversed())
                .limit(i)
                .collect(Collectors.toList());

    }

    /* 칭찬 칭찬 비율(한달동안 받은 칭찬 종류 각각 비율) */
    public List<PraiseRatioResponseDTO> getPraiseRatioForLastMonth(Long userId) {

        // 한 달 날짜 범위 설정
        LocalDateTime endDateTime = LocalDateTime.now();
        LocalDateTime startDateTime = endDateTime.minusMonths(1);

        // 칭찬 타입 별로 count
        List<Object[]> result = praiseReceiverRepository.countPraiseTypeByUserInMonth(userId,startDateTime,endDateTime);

        // 총 받은 칭찬 개수 계산
        int total = result.stream()
                .mapToInt(row ->((Long) row[1]).intValue())
                .sum();

        // 받은 칭찬이 없으면 빈 리스트 반환
        if (total == 0){
            return Collections.emptyList();
        }

        // 비율 계산 후 DTO 변환
        return result.stream()
                .map(row ->{
                    PraiseType type = (PraiseType) row[0];
                    Long count = (Long) row[1];
                    int percentage = (int) Math.round(count*100.0/total);
                    return PraiseRatioResponseDTO.from(type,percentage);
                })
                .collect(Collectors.toList());
    }

    /* 최근 칭찬 보낸 유저 조회 */
    public List<RecentPraiseSenderResponseDTO> getRecentPraiseSenders(Long userId) {

        List<Praise> latestPraises = praiseReceiverRepository.findLatestPraisePerSender(userId);

        // 칭찬 받은게 없을 경우
        if (latestPraises.isEmpty()) {
            log.info("No recent praises found for user: {}", userId);
            return Collections.emptyList();
        }

        // 중복 제거된 보낸 유저 3명만 추출
        return latestPraises.stream()
                .sorted(Comparator.comparing(Praise::getCreatedAt).reversed())
                .map(Praise::getSender)
                .distinct()
                .limit(3)
                .map(RecentPraiseSenderResponseDTO::from)
                .collect(Collectors.toList());
    }

    /* 칭찬 상세 조회 */
    public PraiseDetailResponseDTO getPraiseDetail(Long praiseId) {

        // 칭찬 엔티티 조회
        Praise praise = praiseRepository.findById(praiseId).orElseThrow(() -> new PraiseNotFoundException());

        // 칭찬 받은 사람 리스트 조회
        List<PraiseReceiver> receiverList = praiseReceiverRepository.findByPraise(praise);

        // 댓글 목록 조회
        List<PraiseComment> commentList = commentService.getCommentsByPraise(praise);

        // 이모지 반응 수 조회
        List<Object[]> counts = praiseEmojiReactionRepository.countGroupedByEmoji(praise);
        Map<String, Integer> emojiCount = counts.stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> ((Long) row[1]).intValue()
                ));

        // 댓글 별 이모지 반응 수 조회
        Map<Long, Map<String, Integer>> commentEmojiMap = commentList.stream()
                .collect(Collectors.toMap(
                        PraiseComment::getId,
                        comment -> {
                            List<Object[]> emojiData = commentEmojiReactionRepository.countGroupedByEmoji(comment);
                            return emojiData.stream().collect(Collectors.toMap(
                                    row -> (String) row[0],
                                    row -> ((Long) row[1]).intValue()));
                        }));

        return PraiseDetailResponseDTO.from(praise,receiverList,commentList,emojiCount,commentEmojiMap);
    }
}
