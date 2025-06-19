package org.example.hugmeexp.domain.studydiary.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.studydiary.dto.request.StudyDiaryCreateRequest;
import org.example.hugmeexp.domain.studydiary.entity.StudyDiary;
import org.example.hugmeexp.domain.studydiary.exception.UserNotFoundForStudyDiaryException;
import org.example.hugmeexp.domain.studydiary.repository.StudyDiaryRepository;
import org.example.hugmeexp.global.common.repository.UserRepository;
import org.example.hugmeexp.global.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyDiaryService {

    private final UserRepository userRepository;
    private final StudyDiaryRepository studyDiaryRepository;

    @Transactional
    public Long createStudyDiary(StudyDiaryCreateRequest createRequest, UserDetails userDetails){
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(UserNotFoundForStudyDiaryException::new);

        StudyDiary createdStudyDiary = StudyDiary.builder()
                .title(createRequest.getTitle())
                .content(createRequest.getTitle())
                .isCreated(true)
                .user(user)
                .build();

        return createdStudyDiary.getId();
    }

    public List<StudyDiary> getStudyDiaries(String sort, Pageable pageable) {

        return
    }

    public List<StudyDiary> searchStudyDiaries(String keyword, Pageable pageable) {
    }

    public StudyDiary getStudyDiary(Long id) {
    }

    public List<StudyDiary> getUserStudyDiaries(Long userId, Pageable pageable) {
    }

    public List<StudyDiary> getSimilarStudyDiaries(Long id) {
    }

    public Long saveDraft(StudyDiaryCreateRequest request, UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(UserNotFoundForStudyDiaryException::new);

        
        StudyDiary createdStudyDiary = StudyDiary.builder()
                .title(request.getTitle())
                .content(request.getTitle())
                .isCreated(false)
                .user(user)
                .build();

    }

    public Object getWeekStatus(Long userId) {
    }

    public Object exportStudyDiaries(Long userId) {
    }

    public Long createComment(Long id, StudyDiaryCreateRequest request, User user) {
    }
}
