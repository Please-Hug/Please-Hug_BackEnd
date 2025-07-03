package org.example.hugmeexp.domain.studydiary.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.studydiary.dto.request.CommentCreateRequest;
import org.example.hugmeexp.domain.studydiary.dto.request.StudyDiaryCreateRequest;
import org.example.hugmeexp.domain.studydiary.dto.request.StudyDiaryUpdateRequest;
import org.example.hugmeexp.domain.studydiary.dto.request.MarkdownPreviewRequest;
import org.example.hugmeexp.domain.studydiary.dto.response.MarkdownPreviewResponse;
import org.example.hugmeexp.domain.studydiary.dto.response.CommentDetailResponse;
import org.example.hugmeexp.domain.studydiary.dto.response.StudyDiaryDetailResponse;
import org.example.hugmeexp.domain.studydiary.dto.response.StudyDiaryFindAllResponse;
import org.example.hugmeexp.domain.studydiary.dto.response.StudyDiaryMyHomeResponse;
import org.example.hugmeexp.domain.studydiary.dto.response.StudyDiaryWeekStatusResponse;
import org.example.hugmeexp.domain.studydiary.entity.StudyDiary;
import org.example.hugmeexp.domain.studydiary.entity.StudyDiaryComment;
import org.example.hugmeexp.domain.studydiary.exception.StudyDiaryNotFoundException;
import org.example.hugmeexp.domain.studydiary.exception.UnauthorizedAccessException;
import org.example.hugmeexp.domain.studydiary.exception.UserNotFoundForStudyDiaryException;
import org.example.hugmeexp.domain.studydiary.exception.CommentNotFoundException;
import org.example.hugmeexp.domain.studydiary.repository.StudyDiaryCommentRepository;
import org.example.hugmeexp.domain.studydiary.repository.StudyDiaryRepository;
import org.example.hugmeexp.domain.studydiary.repository.StudyDiaryLikeRepository;
import org.example.hugmeexp.domain.studydiary.entity.StudyDiaryLike;
import org.example.hugmeexp.domain.user.entity.User;
import org.example.hugmeexp.domain.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyDiaryService {

    private final UserRepository userRepository;
    private final StudyDiaryRepository studyDiaryRepository;
    private final StudyDiaryCommentRepository studyDiaryCommentRepository;
    private final StudyDiaryLikeRepository studyDiaryLikeRepository;

    @Transactional
    public Long createStudyDiary(StudyDiaryCreateRequest createRequest, UserDetails userDetails){
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(UserNotFoundForStudyDiaryException::new);

        StudyDiary createdStudyDiary = StudyDiary.builder()
                .title(createRequest.getTitle())
                .content(createRequest.getContent())
                .likeCount(0)
                .isCreated(true)
                .user(user)
                .build();
        StudyDiary saved = studyDiaryRepository.save(createdStudyDiary);

        return saved.getId();
    }

    @Transactional
    public Long updateStudyDiary(Long id, StudyDiaryUpdateRequest updateRequest, UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(UserNotFoundForStudyDiaryException::new);

        StudyDiary studyDiary = studyDiaryRepository.findById(id)
                .orElseThrow(StudyDiaryNotFoundException::new);

        checkUser(user, studyDiary);

        studyDiary.updateTitle(updateRequest.getTitle());
        studyDiary.updateContent(updateRequest.getContent());

        return studyDiary.getId();
    }

    @Transactional
    public void deleteStudyDiary(Long id, UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(UserNotFoundForStudyDiaryException::new);

        StudyDiary studyDiary = studyDiaryRepository.findById(id)
                .orElseThrow(StudyDiaryNotFoundException::new);

        checkUser(user, studyDiary);

        studyDiaryRepository.delete(studyDiary);
    }

    public Page<StudyDiaryFindAllResponse> getStudyDiaries(Pageable pageable) {
        Page<StudyDiary> studyDiaries = studyDiaryRepository.findByIsCreatedTrueOrderByCreatedAtDesc(pageable);

        //response로 전환
        Page<StudyDiaryFindAllResponse> studyDiaryFindAllResponsePage = studyDiaries.map(studyDiary -> {    //Page map으로 조작할때에는 stream 없이
            return StudyDiaryFindAllResponse.builder()
                    .id(studyDiary.getId())
                    .name(studyDiary.getUser().getName())
                    .title(studyDiary.getTitle())
                    .content(studyDiary.getContent())
                    .likeNum(studyDiary.getLikeCount())
                    .commentNum(studyDiary.getComments().size())
                    .createdAt(studyDiary.getCreatedAt())
                    .build();
        });

        return studyDiaryFindAllResponsePage;
    }

    public Page<StudyDiaryFindAllResponse> getTodayPopularStudyDiaries(Pageable pageable) {
        // 오늘의 시작과 끝 시간 계산
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

        Page<StudyDiary> studyDiaries = studyDiaryRepository.findTodayPopularStudyDiaries(startOfDay, endOfDay, pageable);

        //response로 전환
        Page<StudyDiaryFindAllResponse> studyDiaryFindAllResponsePage = studyDiaries.map(studyDiary -> {
            return StudyDiaryFindAllResponse.builder()
                    .id(studyDiary.getId())
                    .name(studyDiary.getUser().getName())
                    .title(studyDiary.getTitle())
                    .content(studyDiary.getContent())
                    .likeNum(studyDiary.getLikeCount())
                    .commentNum(studyDiary.getComments().size())
                    .createdAt(studyDiary.getCreatedAt())
                    .build();
        });

        return studyDiaryFindAllResponsePage;
    }

    public List<StudyDiaryFindAllResponse> getStudyDiaryDafts(Pageable pageable, UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(UserNotFoundForStudyDiaryException::new);
        List<StudyDiary> studyDiaryDraftList = studyDiaryRepository.findByIsCreatedFalse(user.getId());

        //response로 전환
        List<StudyDiaryFindAllResponse> studyDiaryFindAllResponsePage = studyDiaryDraftList.stream().map(studyDiary -> {    //Page map으로 조작할때에는 stream 없이
            return StudyDiaryFindAllResponse.builder()
                    .id(studyDiary.getId())
                    .name(studyDiary.getUser().getName())
                    .title(studyDiary.getTitle())
                    .content(studyDiary.getContent())
                    .likeNum(studyDiary.getLikeCount())
                    .commentNum(studyDiary.getComments().size())
                    .createdAt(studyDiary.getCreatedAt())
                    .build();
        }).toList();

        return studyDiaryFindAllResponsePage;
    }

    public Page<StudyDiaryFindAllResponse> searchStudyDiaries(String keyword, Pageable pageable) {
        Page<StudyDiary> studyDiaries = studyDiaryRepository.findByTitleOrContentContaining(keyword, pageable);

        //response로 전환
        Page<StudyDiaryFindAllResponse> studyDiaryFindAllResponsePage = studyDiaries.map(studyDiary -> {    //Page map으로 조작할때에는 stream 없이
            return StudyDiaryFindAllResponse.builder()
                    .id(studyDiary.getId())
                    .name(studyDiary.getUser().getName())
                    .title(studyDiary.getTitle())
                    .content(studyDiary.getContent())
                    .likeNum(studyDiary.getLikeCount())
                    .commentNum(studyDiary.getComments().size())
                    .createdAt(studyDiary.getCreatedAt())
                    .build();
        });

        return studyDiaryFindAllResponsePage;
    }

    public StudyDiaryDetailResponse getStudyDiary(Long id) {
        StudyDiary studyDiary = studyDiaryRepository.findById(id)
                .orElseThrow(StudyDiaryNotFoundException::new);

        StudyDiaryDetailResponse studyDiaryDetailResponse = StudyDiaryDetailResponse.builder()
                .id(studyDiary.getId())
                .userId(studyDiary.getUser().getId())
                .name(studyDiary.getUser().getName())
                .title(studyDiary.getTitle())
                .content(studyDiary.getContent())
                .likeNum(studyDiary.getLikeCount())
                .createdAt(studyDiary.getCreatedAt())
                .commentList(studyDiary.getComments().stream()
                        .map(CommentDetailResponse::buildToResponse)
                        .toList())
                .build();

        return studyDiaryDetailResponse;
    }

    public List<StudyDiaryFindAllResponse> getUserStudyDiaries(Long userId, Pageable pageable) {
        User findUser = userRepository.findById(userId).orElseThrow(UserNotFoundForStudyDiaryException::new);
        List<StudyDiary> byUser = studyDiaryRepository.findByUser(findUser.getId());

        List<StudyDiaryFindAllResponse> studyDiaryFindAllResponses = byUser.stream().map(studyDiary -> {    //Page map으로 조작할때에는 stream 없이
            return StudyDiaryFindAllResponse.builder()
                    .id(studyDiary.getId())
                    .name(studyDiary.getUser().getName())
                    .title(studyDiary.getTitle())
                    .content(studyDiary.getContent())
                    .likeNum(studyDiary.getLikeCount())
                    .commentNum(studyDiary.getComments().size())
                    .createdAt(studyDiary.getCreatedAt())
                    .build();
        }).toList();

        return studyDiaryFindAllResponses;
    }

    public List<StudyDiaryFindAllResponse> getMyStudyDiaries(UserDetails userDetails, Pageable pageable) {
        User findUser = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(UserNotFoundForStudyDiaryException::new);
        List<StudyDiary> byUser = studyDiaryRepository.findByUser(findUser.getId());

        List<StudyDiaryFindAllResponse> studyDiaryFindAllResponses = byUser.stream().map(studyDiary -> {    //Page map으로 조작할때에는 stream 없이
            return StudyDiaryFindAllResponse.builder()
                    .id(studyDiary.getId())
                    .name(studyDiary.getUser().getName())
                    .title(studyDiary.getTitle())
                    .content(studyDiary.getContent())
                    .likeNum(studyDiary.getLikeCount())
                    .commentNum(studyDiary.getComments().size())
                    .createdAt(studyDiary.getCreatedAt())
                    .build();
        }).toList();

        return studyDiaryFindAllResponses;
    }

    public Page<StudyDiaryMyHomeResponse> getMyRecentStudyDiaries(UserDetails userDetails, Pageable pageable) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(UserNotFoundForStudyDiaryException::new);

        // 30일 전 날짜 계산
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        
        Page<StudyDiary> studyDiaries = studyDiaryRepository.findByUserIdAndCreatedAtAfterOrderByCreatedAtDesc(
                user.getId(), thirtyDaysAgo, pageable);

        // 현재 날짜와 시간
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();

        // Response DTO로 변환
        Page<StudyDiaryMyHomeResponse> responsePages = studyDiaries.map(studyDiary -> {
            // 오늘로부터 몇일 전인지 계산 (LocalDateTime 사용)
            LocalDate createdDate = studyDiary.getCreatedAt().toLocalDate();
            long daysAgo = calculateDaysAgo(createdDate, today);
            
            return StudyDiaryMyHomeResponse.builder()
                    .id(studyDiary.getId())
                    .title(studyDiary.getTitle())
                    .createdAt(studyDiary.getCreatedAt())
                    .daysAgo(daysAgo)
                    .build();
        });

        return responsePages;
    }

    //추후 구현(elastic search 필요)

//    public List<StudyDiary> getSimilarStudyDiaries(Long id) {
//    }
    @Transactional
    public Long saveDraft(StudyDiaryCreateRequest request, UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(UserNotFoundForStudyDiaryException::new);

        StudyDiary createdStudyDiary = StudyDiary.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .isCreated(false)
                .user(user)
                .build();

        StudyDiary saved = studyDiaryRepository.save(createdStudyDiary);

        return saved.getId();
    }

    public StudyDiaryWeekStatusResponse getWeekStatus(Long userId) {
        //이번주 날짜 구하기
        LocalDateTime startOfWeek = LocalDateTime.now().with(DayOfWeek.MONDAY).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfWeek = startOfWeek.plusDays(6).withHour(23).withMinute(59).withSecond(59);
        //초기 response 객체 생성
        StudyDiaryWeekStatusResponse response = StudyDiaryWeekStatusResponse.builder()
                .sunday(false)
                .monday(false)
                .tuesday(false)
                .wednesday(false)
                .thursday(false)
                .friday(false)
                .saturday(false)
                .todayStudyDiaryNum(0)
                .totalLike(0)
                .build();

        List<StudyDiary> weekStudyDiaries = studyDiaryRepository.findByUserIdAndCreatedAtBetween(userId, startOfWeek, endOfWeek);

        //주간 작성상황 작성
        weekStudyDiaries.stream().forEach(studyDiary -> {
            switch (studyDiary.getCreatedAt().getDayOfWeek()){
                case SUNDAY -> response.setSunday(true);
                case MONDAY -> response.setMonday(true);
                case TUESDAY -> response.setTuesday(true);
                case WEDNESDAY -> response.setWednesday(true);
                case THURSDAY -> response.setThursday(true);
                case FRIDAY -> response.setFriday(true);
                case SATURDAY -> response.setSaturday(true);
            }
        });

        //오늘 쓴 일기 갯수
        weekStudyDiaries.stream().forEach(studyDiary -> {
            if(studyDiary.getCreatedAt().toLocalDate().equals(LocalDate.now()))
                response.setTodayStudyDiaryNum(response.getTodayStudyDiaryNum() + 1);
        });

        //총 Like 갯수 계산
        List<StudyDiary> byUserAllStudyDiaryList = studyDiaryRepository.findByUser(userId);
        //stream 내부에서는 외부 변수 수정 불가, 아래와 같은 방법이 정석이라고 함
        int likeCount = byUserAllStudyDiaryList.stream()
                .mapToInt(StudyDiary::getLikeCount)
                .sum();
        response.setTotalLike(likeCount);

        return response;
    }

    public StudyDiaryWeekStatusResponse getMyWeekStatus(UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(UserNotFoundForStudyDiaryException::new);
        //이번주 날짜 구하기
        LocalDateTime startOfWeek = LocalDateTime.now().with(DayOfWeek.MONDAY).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfWeek = startOfWeek.plusDays(6).withHour(23).withMinute(59).withSecond(59);
        //초기 response 객체 생성
        StudyDiaryWeekStatusResponse response = StudyDiaryWeekStatusResponse.builder()
                .sunday(false)
                .monday(false)
                .tuesday(false)
                .wednesday(false)
                .thursday(false)
                .friday(false)
                .saturday(false)
                .todayStudyDiaryNum(0)
                .totalLike(0)
                .build();

        log.info("start {} end {}", startOfWeek, endOfWeek);
        List<StudyDiary> weekStudyDiaries = studyDiaryRepository.findByUserIdAndCreatedAtBetween(user.getId(), startOfWeek, endOfWeek);
        log.info("week diaries {}", weekStudyDiaries.size());

        //주간 작성상황 작성
        weekStudyDiaries.stream().forEach(studyDiary -> {
            switch (studyDiary.getCreatedAt().getDayOfWeek()){
                case SUNDAY -> response.setSunday(true);
                case MONDAY -> response.setMonday(true);
                case TUESDAY -> response.setTuesday(true);
                case WEDNESDAY -> response.setWednesday(true);
                case THURSDAY -> response.setThursday(true);
                case FRIDAY -> response.setFriday(true);
                case SATURDAY -> response.setSaturday(true);
            }
        });

        //오늘 쓴 일기 갯수
        weekStudyDiaries.stream().forEach(studyDiary -> {
            if(studyDiary.getCreatedAt().toLocalDate().equals(LocalDate.now()))
                response.setTodayStudyDiaryNum(response.getTodayStudyDiaryNum() + 1);
        });

        //총 Like 갯수 계산
        List<StudyDiary> byUserAllStudyDiaryList = studyDiaryRepository.findByUser(user.getId());
        //stream 내부에서는 외부 변수 수정 불가, 아래와 같은 방법이 정석이라고 함
        int likeCount = byUserAllStudyDiaryList.stream()
                .mapToInt(StudyDiary::getLikeCount)
                .sum();
        response.setTotalLike(likeCount);

        return response;
    }
    //추후구현
//    public Object exportStudyDiaries(Long userId) {

//    }

    @Transactional
    public Long createComment(Long studyDiaryId, CommentCreateRequest request, UserDetails userDetails) {
        StudyDiary studyDiary = studyDiaryRepository.findById(studyDiaryId).orElseThrow(StudyDiaryNotFoundException::new);
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(UserNotFoundForStudyDiaryException::new);
        StudyDiaryComment comment = StudyDiaryComment.builder()
                .studyDiary(studyDiary)
                .user(user)
                .content(request.getContent())
                .build();
        studyDiaryCommentRepository.save(comment);
        return comment.getId();
    }

    @Transactional
    public void deleteComment(Long studyDiaryId, Long commentId, UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(UserNotFoundForStudyDiaryException::new);

        // 배움일기 존재 여부 확인
        StudyDiary studyDiary = studyDiaryRepository.findById(studyDiaryId)
                .orElseThrow(StudyDiaryNotFoundException::new);

        // 댓글 존재 여부 확인
        StudyDiaryComment comment = studyDiaryCommentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);

        if(!user.getId().equals(comment.getUser().getId())){
            throw new UnauthorizedAccessException();
        }

        studyDiaryCommentRepository.delete(comment);
    }

    @Transactional
    public int toggleLike(Long studyDiaryId, UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(UserNotFoundForStudyDiaryException::new);

        StudyDiary studyDiary = studyDiaryRepository.findById(studyDiaryId)
                .orElseThrow(StudyDiaryNotFoundException::new);

        // 이미 좋아요를 눌렀는지 확인
        Optional<StudyDiaryLike> existingLike = studyDiary.getLikes()
                .stream().filter(like -> like.getUser().getId().equals(user.getId()))
                .findFirst();
//        log.info("Log userDetails {}, Like present {}", userDetails.getUsername(), existingLike.isPresent());
//        log.info("Log user {}, Like present {}", user.getId(), existingLike.isPresent());

        if (existingLike.isPresent()) {
//            log.info("Like {}", existingLike.get().getId());
            // 좋아요 취소
            return studyDiary.deleteLike(user.getId());
        } else {
            return studyDiary.addLike(user);
        }
        //return 값으로 최신 좋아요 갯수
    }

    private void checkUser(User user, StudyDiary studyDiary) {
        if(!user.getId().equals(studyDiary.getUser().getId())){
            throw new UnauthorizedAccessException();
        }
    }

    private long calculateDaysAgo(LocalDate createdDate, LocalDate today) {
        // 년, 월, 일을 각각 비교하여 날짜 차이 계산
        int yearDiff = today.getYear() - createdDate.getYear();
        int monthDiff = today.getMonthValue() - createdDate.getMonthValue();
        int dayDiff = today.getDayOfMonth() - createdDate.getDayOfMonth();
        
        // 전체 일수로 변환
        if (yearDiff == 0 && monthDiff == 0) {
            // 같은 년, 월인 경우
            return Math.abs(dayDiff);
        } else {
            // 다른 년도 또는 월인 경우 - 더 정확한 계산
            long totalDays = 0;
            LocalDate start = createdDate;
            LocalDate end = today;
            
            // 시작 날짜가 더 늦은 경우 순서 바꾸기
            if (start.isAfter(end)) {
                start = today;
                end = createdDate;
            }
            
            // 날짜별로 하나씩 증가시키면서 계산
            while (!start.equals(end)) {
                start = start.plusDays(1);
                totalDays++;
            }
            
            return totalDays;
        }
    }

    /**
     * 마크다운 미리보기 생성
     */
    public MarkdownPreviewResponse previewMarkdown(MarkdownPreviewRequest request) {
        String markdownContent = request.getMarkdownContent();
        
        // 간단한 마크다운 -> HTML 변환 (실제 구현시에는 마크다운 라이브러리 사용 권장)
        String htmlContent = convertMarkdownToHtml(markdownContent);
        
        // 글자 수 계산
        int characterCount = markdownContent.length();
        
        // 단어 수 계산 (공백 기준)
        int wordCount = markdownContent.trim().isEmpty() ? 0 : markdownContent.trim().split("\\s+").length;
        
        return MarkdownPreviewResponse.builder()
                .markdownContent(markdownContent)
                .htmlContent(htmlContent)
                .characterCount(characterCount)
                .wordCount(wordCount)
                .build();
    }

    /**
     * 간단한 마크다운 -> HTML 변환
     * 실제 운영시에는 commonmark, flexmark 등의 라이브러리 사용 권장
     */
    private String convertMarkdownToHtml(String markdown) {
        if (markdown == null || markdown.trim().isEmpty()) {
            return "";
        }
        
        String html = markdown;
        
        // 헤딩 변환
        html = html.replaceAll("^### (.*$)", "<h3>$1</h3>");
        html = html.replaceAll("^## (.*$)", "<h2>$1</h2>");
        html = html.replaceAll("^# (.*$)", "<h1>$1</h1>");
        
        // 굵은 글씨
        html = html.replaceAll("\\*\\*(.*?)\\*\\*", "<strong>$1</strong>");
        
        // 기울임 글씨
        html = html.replaceAll("\\*(.*?)\\*", "<em>$1</em>");
        
        // 코드 블록 (간단한 버전)
        html = html.replaceAll("```java([\\s\\S]*?)```", "<pre><code class=\"language-java\">$1</code></pre>");
        html = html.replaceAll("```([\\s\\S]*?)```", "<pre><code>$1</code></pre>");
        
        // 인라인 코드
        html = html.replaceAll("`(.*?)`", "<code>$1</code>");
        
        // 이미지
        html = html.replaceAll("!\\[(.*?)\\]\\((.*?)\\)", "<img src=\"$2\" alt=\"$1\" />");
        
        // 링크
        html = html.replaceAll("\\[(.*?)\\]\\((.*?)\\)", "<a href=\"$2\">$1</a>");
        
        // 줄바꿈 처리
        html = html.replace("\n", "<br/>");
        
        return html;
    }
}
