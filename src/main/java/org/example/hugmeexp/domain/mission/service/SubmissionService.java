package org.example.hugmeexp.domain.mission.service;

import org.example.hugmeexp.domain.mission.dto.request.SubmissionFeedbackRequest;
import org.example.hugmeexp.domain.mission.dto.request.SubmissionUploadRequest;
import org.example.hugmeexp.domain.mission.dto.response.SubmissionResponse;
import org.springframework.web.multipart.MultipartFile;

public interface SubmissionService {
    void submitChallenge(Long userMissionId, SubmissionUploadRequest submissionUploadRequest, MultipartFile file);

    SubmissionResponse getSubmissionByMissionId(Long userMissionId);

    void updateSubmissionFeedback(Long userMissionId, SubmissionFeedbackRequest submissionFeedbackRequest);

    void receiveReward(Long userMissionId, String username);
}
