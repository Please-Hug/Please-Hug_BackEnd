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

    /* 기본 - 날짜 조회 */
    // /api/v1/praises/search?startDate=OOOO-OO-OO&endDate=OOOO-OO-OO
    @Operation(summary = "날짜 기준 칭찬 게시물 조회", description = "날짜를 기준으로 칭찬을 받거나 보낸 게시물들이 조회됩니다")
    @GetMapping("/search")
    public ResponseEntity<Response<List<PraiseResponseDTO>>> getDatePraises(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate endDate){

        log.info("Received praise search request: startDate={}, endDate={}", startDate, endDate);

        if (startDate.isAfter(endDate)) {
            log.warn("Invalid date range: startDate is after endDate");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.<List<PraiseResponseDTO>>builder()
                            .message("startDate 은 endDate 보다 이후일 수 없습니다.")
                            .data(List.of())
                            .build());
        }

        List<PraiseResponseDTO> result = praiseService.findByDateRange(startDate,endDate);

        log.info("Total praises found in date range: {} entries", result.size());

        Response<List<PraiseResponseDTO>> response = Response.<List<PraiseResponseDTO>>builder()
                .message("날짜 기준 조회 성공")
                .data(result)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    /* 날짜 조회 + 키워드 포함 검색 조회 */
//    @GetMapping("/search")
//    @Operation(summary = "날짜 기준 유저 키워드 검색 조회", description = "날짜를 기준으로 자신이 칭찬을 보내거나 칭찬을 받은 유저의 이름을 검색하면 키워드 포함 조회가 된다.")
//    public ResponseEntity<Response<List<PraiseResponseDTO>>> searchPraises(
//            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate startDate,
//            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate endDate,
//            @RequestParam("keyword") String keyword){
//
//        log.info("Received praise search request, and keyword : startDate={}, endDate={}", startDate, endDate);
//        log.info("칭찬 검색 keyword : {} " , keyword);
//
//        List<PraiseResponseDTO> filteringResult = praiseService.searchByKeywordAndDate(startDate,endDate,keyword);
//
//        log.info("검색 필터링 결과 보이는 건수 : {} " , filteringResult.size());
//
//        Response<List<PraiseResponseDTO>> response = Response.<List<PraiseResponseDTO>>builder()
//                .message("보낸 사람 / 받은 사람 필터링 성공")
//                .data(filteringResult)
//                .build();
//        return ResponseEntity.ok(response);
//    }

    /* 칭찬 반응 좋은 칭찬글 */
    /* 칭찬 칭찬 비율 */
    /* 칭찬 최근 칭찬 유저 */

}
