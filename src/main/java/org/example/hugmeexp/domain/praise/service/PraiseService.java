package org.example.hugmeexp.domain.praise.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.praise.dto.PraiseRatioResponseDTO;
import org.example.hugmeexp.domain.praise.dto.PraiseRequestDTO;
import org.example.hugmeexp.domain.praise.dto.PraiseResponseDTO;
import org.example.hugmeexp.domain.praise.dto.RecentPraiseSenderResponseDTO;
import org.example.hugmeexp.domain.praise.entity.Praise;
import org.example.hugmeexp.domain.praise.enums.PraiseType;
import org.example.hugmeexp.domain.praise.exception.UserNotFoundInPraiseException;
import org.example.hugmeexp.domain.praise.mapper.PraiseMapper;
import org.example.hugmeexp.domain.praise.repository.CommentRepository;
import org.example.hugmeexp.domain.praise.repository.PraiseEmojiReactionRepository;
import org.example.hugmeexp.domain.praise.repository.PraiseRepository;
import org.example.hugmeexp.domain.user.repository.UserRepository;
import org.example.hugmeexp.domain.user.entity.User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PraiseService {

    private final PraiseMapper praiseMapper;
    private final PraiseRepository praiseRepository;
    private final CommentRepository commentRepository;
    private final PraiseEmojiReactionRepository praiseEmojiReactionRepository;
    private final UserRepository userRepository;


    /* 칭찬 생성 */
    public PraiseResponseDTO createPraise(PraiseRequestDTO praiseRequestDTO, User senderId) {

        try {

            User receiverId = userRepository.findByUsername(praiseRequestDTO.getReceiverUsername()).
                    orElseThrow(() -> new UserNotFoundInPraiseException());

            // DTO -> Entity
             Praise praise = praiseMapper.toEntity(praiseRequestDTO, senderId, receiverId);

            // DB 에 저장
            Praise saved = praiseRepository.save(praise);

            // Entity -> DTO
            return praiseMapper.toDTO(saved);
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
            // 나와 관련된 칭찬만
            praiseList = praiseRepository.findByDateRangeAndUser(startDateTime,endDateTime,currentUser);
        }else {
            // 전체 칭찬 조회
            praiseList = praiseRepository.findByCreatedAtBetween(startDateTime, endDateTime);
        }


        return praiseList.stream()
                .map(praise -> {
                    long commentCount = commentRepository.countByPraise(praise);
                    List<Object[]> counts = praiseEmojiReactionRepository.countGroupedByEmoji(praise);

                    Map<String, Integer> emojiCount = counts.stream()
                            .collect(Collectors.toMap(
                                    row -> (String) row[0],
                                    row -> ((Long) row[1]).intValue()
                            ));
                    return PraiseResponseDTO.from(praise,commentCount,emojiCount);

                }).collect(Collectors.toList());
    }

    /* 날짜 조회 + 나와 관련된 칭찬 조건 + keyword 조건 */
    public List<PraiseResponseDTO> searchByKeywordAndDate(LocalDate startDate, LocalDate endDate, User currentUser, boolean me, String keyword) {

        LocalDateTime startDateTime = startDate.atStartOfDay();    // 2025-06-01 00:00:00
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);    // 2025-06-18 23:59:59.999

        List<Praise> praiseList;

        if(me){
            praiseList = praiseRepository.findByDateAndUserAndKeyword(startDateTime, endDateTime, currentUser, keyword);
        } else{
            praiseList = praiseRepository.findByDateAndKeyword(startDateTime,endDateTime,keyword);
        }

        return praiseList.stream()
                .map(praise -> {
                    long commentCount = commentRepository.countByPraise(praise);

                    List<Object[]> counts = praiseEmojiReactionRepository.countGroupedByEmoji(praise);
                    Map<String, Integer> emojiCount = counts.stream()
                            .collect(Collectors.toMap(
                                    row -> (String) row[0],
                                    row -> ((Long) row[1]).intValue()
                            ));

                    return PraiseResponseDTO.from(praise,commentCount,emojiCount);
                }).collect(Collectors.toList());

    }


    /* 칭찬 반응 좋은 칭찬글 */
    public List<PraiseResponseDTO> findPopularPraises(LocalDate startDate, LocalDate endDate, int i) {

        LocalDateTime startDateTime = startDate.atStartOfDay();    // 2025-06-01 00:00:00
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);    // 2025-06-18 23:59:59.999

        // 해당 기간 내 칭찬글 전체 조회
        List<Praise> praiseList = praiseRepository.findByCreatedAtBetween(startDateTime, endDateTime);

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

                    return PraiseResponseDTO.from(praise,commentCount,emojiCount);

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
        List<Object[]> result = praiseRepository.countPraiseTypeByUserInMonth(userId,startDateTime,endDateTime);

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

        List<Praise> latestPraises = praiseRepository.findLatestPraisePerSender(userId);

        // 중복 제거된 보낸 유저 3명만 추출
        return latestPraises.stream()
                .map(Praise::getSender)
                .distinct()
                .limit(3)
                .map(RecentPraiseSenderResponseDTO::from)
                .collect(Collectors.toList());
    }
}
