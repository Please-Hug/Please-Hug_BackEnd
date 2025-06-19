package org.example.hugmeexp.domain.praise.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.praise.dto.PraiseRequestDTO;
import org.example.hugmeexp.domain.praise.dto.PraiseResponseDTO;
import org.example.hugmeexp.domain.praise.service.PraiseService;
import org.example.hugmeexp.global.common.response.Response;
import org.example.hugmeexp.global.entity.User;
import org.example.hugmeexp.global.security.CustomUserDetails;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j    // 로깅 어노테이션
@RestController
@RequestMapping("/api/v1/praises")
@RequiredArgsConstructor
@Tag(name = "Praise" , description = "칭찬 관련 API")
public class PraiseController {

    private final PraiseService praiseService;

    /* 칭찬 생성 */
    @Operation(summary = "칭찬 생성", description = "새로운 칭찬을 생성합니다")
    @PostMapping
    public ResponseEntity<Response<PraiseResponseDTO>> createPraise(
            @RequestBody @Valid PraiseRequestDTO praiseRequestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails){

        log.info("Praise creation request : {} ", praiseRequestDTO);
        log.debug("Current logged-in user : {}", userDetails.getUser().getUsername());

        // 로그인된 사용자 정보에서 User 꺼낸다
        User senderId = userDetails.getUser();

        PraiseResponseDTO result = praiseService.createPraise(praiseRequestDTO,senderId );

        log.info("Praise saved successfully : {} " , result);

        Response<PraiseResponseDTO> response = Response.<PraiseResponseDTO>builder()
                .message("작성 완료")
                .data(result)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /* 기본 - 날짜 조회 / ME / keyword */
    // /api/v1/praises/search?startDate=OOOO-OO-OO&endDate=OOOO-OO-OO
    @Operation(summary = "날짜 기준 칭찬 게시물 조회", description = "날짜, 로그인 유저 여부(me), 키워드(keyword)로 칭찬을 조회합니다")
    @GetMapping("/search")
    public ResponseEntity<Response<List<PraiseResponseDTO>>> getDatePraises(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate endDate,
            @RequestParam(name = "me", required = false)boolean me,
            @RequestParam(name = "keyword",required = false) String keyword,
            @AuthenticationPrincipal CustomUserDetails userDetails){

        log.info("Received praise search request: startDate={}, endDate={}", startDate, endDate);

        if (startDate.isAfter(endDate)) {
            log.warn("Invalid date range: startDate is after endDate");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.<List<PraiseResponseDTO>>builder()
                            .message("startDate 은 endDate 보다 이후일 수 없습니다.")
                            .data(List.of())
                            .build());
        }

        // 나에게 관련관 것만 보기 위한 조건 유저
        User currentUser = userDetails.getUser();
        List<PraiseResponseDTO> result;

        // keyword 가 있는 경우 분기 처리
        if(StringUtils.hasText(keyword)){
            result = praiseService.searchByKeywordAndDate(startDate,endDate,currentUser,me,keyword);
        }else {
            result = praiseService.findByDateRange(startDate,endDate,currentUser,me);
        }

        log.info("Total praises found in date range: {} entries", result.size());

        Response<List<PraiseResponseDTO>> response = Response.<List<PraiseResponseDTO>>builder()
                .message("날짜 기준 조회 성공")
                .data(result)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /* 칭찬 반응 좋은 칭찬글 */

    /* 칭찬 칭찬 비율 */
    /* 칭찬 최근 칭찬 유저 */

}
