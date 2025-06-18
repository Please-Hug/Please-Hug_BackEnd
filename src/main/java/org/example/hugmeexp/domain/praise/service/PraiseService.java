package org.example.hugmeexp.domain.praise.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.praise.dto.PraiseRequestDTO;
import org.example.hugmeexp.domain.praise.dto.PraiseResponseDTO;
import org.example.hugmeexp.domain.praise.entity.Praise;
import org.example.hugmeexp.domain.praise.exception.PraiseNotFoundException;
import org.example.hugmeexp.domain.praise.mapper.PraiseMapper;
import org.example.hugmeexp.domain.praise.repository.CommentRepository;
import org.example.hugmeexp.domain.praise.repository.PraiseEmojiReactionRepository;
import org.example.hugmeexp.domain.praise.repository.PraiseRepository;
import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.example.hugmeexp.global.common.repository.UserRepository;
import org.example.hugmeexp.global.entity.User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

            User receiverId = userRepository.findByName(praiseRequestDTO.getReceiverName()).
                    orElseThrow(() -> new PraiseNotFoundException());

            // DTO -> Entity
             Praise praise = praiseMapper.toEntity(praiseRequestDTO, senderId, receiverId);

            // DB 에 저장
            Praise saved = praiseRepository.save(praise);

            // Entity -> DTO
            return praiseMapper.toDTO(saved);
        } catch (Exception e){
            log.error("칭찬 생성 중 예외 발생: {}", e.getMessage(),e);
            throw new PraiseNotFoundException();
        }

    }

    /* 기본 - 날짜 조회 */
    public List<PraiseResponseDTO> findByDateRange(LocalDate startDate, LocalDate endDate) {

        LocalDateTime startDateTime = startDate.atStartOfDay();    // 2025-06-01 00:00:00
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);    // 2025-06-18 23:59:59.999

        List<Praise> praiseList = praiseRepository.findByCreatedAtBetween(startDateTime, endDateTime);

        return praiseList.stream()
                .map(praise -> {
                    int commentCount = commentRepository.countByPraise(praise);
                    Map<String, Integer> emojiCount = praiseEmojiReactionRepository.countGroupedMapByPraise(praise);
                    return PraiseResponseDTO.from(praise,commentCount,emojiCount);
                }).collect(Collectors.toList());
    }


//    /* 칭찬 필터링 검색 조회 */
//    public List<PraiseResponseDTO> searchByKeyword(String keyword) {
//
//        return praiseRepository.findBySenderIdContainingOrReceiverIdContaining(keyword, keyword).stream()
//                .map(praise -> praiseMapper.toDTO(praise))
//                .collect(Collectors.toList());
//    }


}
