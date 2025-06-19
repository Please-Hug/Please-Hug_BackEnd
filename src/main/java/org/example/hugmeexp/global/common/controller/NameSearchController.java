package org.example.hugmeexp.global.common.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.global.common.response.Response;
import org.example.hugmeexp.global.common.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class NameSearchController {

    private final UserService userService;

}
