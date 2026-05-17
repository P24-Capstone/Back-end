package com.crewise.backend.domain.user.controller;

import com.crewise.backend.domain.user.dto.UserImgResponse;
import com.crewise.backend.domain.user.dto.UserResponse;
import com.crewise.backend.domain.user.service.UserService;
import com.crewise.backend.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserInfoController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMe(@AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(userService.getUser(userId)));
    }

    @GetMapping("/me/images")
    public ResponseEntity<ApiResponse<List<UserImgResponse>>> getMyImages(@AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(userService.getUserImages(userId)));
    }

    @PostMapping("/me/images")
    public ResponseEntity<ApiResponse<UserImgResponse>> addMyImage(
            @AuthenticationPrincipal String userId,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(ApiResponse.ok(userService.addUserImage(userId, body.get("imgFileKey"))));
    }

    @DeleteMapping("/me/images/{imgId}")
    public ResponseEntity<ApiResponse<Void>> deleteMyImage(
            @AuthenticationPrincipal String userId,
            @PathVariable Long imgId) {
        userService.deleteUserImage(userId, imgId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}