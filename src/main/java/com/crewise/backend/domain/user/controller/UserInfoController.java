package com.crewise.backend.domain.user.controller;

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

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<String>> getMe(@AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(userId));
    }
}