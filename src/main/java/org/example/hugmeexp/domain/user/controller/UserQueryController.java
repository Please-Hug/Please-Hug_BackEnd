package org.example.hugmeexp.domain.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.user.dto.response.UserProfileResponse;
import org.example.hugmeexp.domain.user.entity.User;
import org.example.hugmeexp.domain.user.mapper.UserResponseMapper;
import org.example.hugmeexp.domain.user.service.UserService;
import org.example.hugmeexp.global.common.response.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserQueryController {

    private final UserService userService;

    /*
        name 쿼리 파라미터가 없으면 모든 User 리턴
        name 쿼리 파라미터가 있으면 해당 이름을 가진 User 리턴
    */
    @GetMapping("/api/v1/users")
    public ResponseEntity<Response<List<UserProfileResponse>>> getUsers(@RequestParam(required = false) String name) {

        List<User> users;

        if (name == null || name.isBlank()) users = userService.findAll(); // 전체 유저 조회
        else users = userService.findByNameContaining(name); // 이름 포함 검색

        List<UserProfileResponse> result = users.stream()
                .map(UserResponseMapper::toUserProfileResponse)
                .toList();


        String message = (name == null || name.isBlank()) ? "모든 유저를 불러왔습니다." : "“" + name + "”에 대한 검색 결과";
        return ResponseEntity.ok(Response.<List<UserProfileResponse>>builder()
                .message(message)
                .data(result)
                .build());
    }
}
