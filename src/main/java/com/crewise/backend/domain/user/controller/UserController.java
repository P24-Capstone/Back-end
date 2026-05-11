package com.crewise.backend.domain.user.controller;

import com.crewise.backend.domain.user.dto.LoginRequest;
import com.crewise.backend.domain.user.dto.SignupRequest;
import com.crewise.backend.domain.user.service.UserService;
import com.crewise.backend.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import com.crewise.backend.domain.user.dto.UserUpdateRequest;

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

    @PostMapping("/check-email")
    public ResponseEntity<ApiResponse<Void>> checkEmail(@RequestBody Map<String, String> body) {
        userService.checkEmail(body.get("email"));
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
    // 회원 탈퇴
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@AuthenticationPrincipal String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    // 회원정보 수정
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<Void>> updateUser(
            @AuthenticationPrincipal String userId,
            @RequestBody UserUpdateRequest request) {
        userService.updateUser(userId, request);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}