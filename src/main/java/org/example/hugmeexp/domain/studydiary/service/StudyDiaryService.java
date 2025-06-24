package org.example.hugmeexp.domain.studydiary.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.studydiary.dto.request.CommentCreateRequest;
import org.example.hugmeexp.domain.studydiary.dto.request.StudyDiaryCreateRequest;
import org.example.hugmeexp.domain.studydiary.dto.request.StudyDiaryUpdateRequest;
import org.example.hugmeexp.domain.studydiary.dto.response.CommentDetailResponse;
import org.example.hugmeexp.domain.studydiary.dto.response.StudyDiaryDetailResponse;
import org.example.hugmeexp.domain.studydiary.dto.response.StudyDiaryFindAllResponse;
import org.example.hugmeexp.domain.studydiary.dto.response.StudyDiaryWeekStatusResponse;
import org.example.hugmeexp.domain.studydiary.entity.StudyDiary;
import org.example.hugmeexp.domain.studydiary.entity.StudyDiaryComment;
import org.example.hugmeexp.domain.studydiary.exception.StudyDiaryNotFoundException;
import org.example.hugmeexp.domain.studydiary.exception.UnauthorizedAccessException;
import org.example.hugmeexp.domain.studydiary.exception.UserNotFoundForStudyDiaryException;
import org.example.hugmeexp.domain.studydiary.exception.CommentNotFoundException;
import org.example.hugmeexp.domain.studydiary.repository.StudyDiaryCommentRepository;
import org.example.hugmeexp.domain.studydiary.repository.StudyDiaryRepository;
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

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyDiaryService {

    private final UserRepository userRepository;
    private final StudyDiaryRepository studyDiaryRepository;
    private final StudyDiaryCommentRepository studyDiaryCommentRepository;

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
                    .userName(studyDiary.getUser().getName())
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
                    .userName(studyDiary.getUser().getName())
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
                    .userName(studyDiary.getUser().getName())
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
                .userName(studyDiary.getUser().getName())
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
                    .userName(studyDiary.getUser().getName())
                    .title(studyDiary.getTitle())
                    .content(studyDiary.getContent())
                    .likeNum(studyDiary.getLikeCount())
                    .commentNum(studyDiary.getComments().size())
                    .createdAt(studyDiary.getCreatedAt())
                    .build();
        }).toList();

        return studyDiaryFindAllResponses;
    }

    //추후 구현(elastic search 필요)
//    public List<StudyDiary> getSimilarStudyDiaries(Long id) {
//    }

    @Transactional
    public Long saveDraft(StudyDiaryCreateRequest request, UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(UserNotFoundForStudyDiaryException::new);

        StudyDiary createdStudyDiary = StudyDiary.builder()
                .title(request.getTitle())
                .content(request.getTitle())
                .isCreated(false)
                .user(user)
                .build();

        StudyDiary saved = studyDiaryRepository.save(createdStudyDiary);

        return saved.getId();
    }

    public StudyDiaryWeekStatusResponse getWeekStatus(Long userId) {
        //이번주 날짜 구하기
        LocalDateTime startOfWeek = LocalDateTime.now().with(DayOfWeek.SUNDAY).withHour(0).withMinute(0).withSecond(0);
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

        if(user.getId().equals(comment.getUser().getId())){
            throw new UnauthorizedAccessException();
        }

        studyDiaryCommentRepository.delete(comment);
    }

    private void checkUser(User user, StudyDiary studyDiary) {
        if(!user.getId().equals(studyDiary.getUser().getId())){
            throw new UnauthorizedAccessException();
        }
    }

}
