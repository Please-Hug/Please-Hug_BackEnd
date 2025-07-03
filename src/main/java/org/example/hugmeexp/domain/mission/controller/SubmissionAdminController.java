package org.example.hugmeexp.domain.mission.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Submissions Admin", description = "제출 정보 조회, 피드백, 보상 수령 API")
@RestController
@RequestMapping("/api/v1/admin/submissions")
@RequiredArgsConstructor
public class SubmissionAdminController {

}
