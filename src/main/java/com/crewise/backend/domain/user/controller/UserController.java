package com.crewise.backend.domain.user.controller;

import com.crewise.backend.domain.user.dto.LoginRequest;
import com.crewise.backend.domain.user.dto.SignupRequest;
import com.crewise.backend.domain.user.service.UserService;
import com.crewise.backend.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@Valid @RequestBody SignupRequest request) {
        userService.signup(request);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@Valid @RequestBody LoginRequest request) {
        String token = userService.login(request);
        return ResponseEntity.ok(ApiResponse.ok(token));
    }

}