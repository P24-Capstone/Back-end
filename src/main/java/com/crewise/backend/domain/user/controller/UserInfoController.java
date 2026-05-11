package com.crewise.backend.domain.user.controller;

import com.crewise.backend.domain.user.dto.UserResponse;
import com.crewise.backend.domain.user.service.UserService;
import com.crewise.backend.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserInfoController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMe(@AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(userService.getUser(userId)));
    }
}