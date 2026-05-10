package com.crewise.backend.domain.member.controller;

import com.crewise.backend.domain.member.dto.MemberSignupRequest;
import com.crewise.backend.domain.member.dto.MemberSignupResponse;
import com.crewise.backend.domain.member.service.MemberSignupService;
import com.crewise.backend.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/signups")
@RequiredArgsConstructor
public class MemberSignupController {

    private final MemberSignupService memberSignupService;

    // 가입 신청
    @PostMapping
    public ResponseEntity<ApiResponse<MemberSignupResponse>> apply(
            @Valid @RequestBody MemberSignupRequest request,
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(memberSignupService.apply(request, userId)));
    }

    // 가입 신청 목록 조회 (모임장용)
    @GetMapping
    public ResponseEntity<ApiResponse<List<MemberSignupResponse>>> getSignups(
            @RequestParam String teamId) {
        return ResponseEntity.ok(ApiResponse.ok(memberSignupService.getSignups(teamId)));
    }

    // 가입 승인
    @PatchMapping("/{signupId}/approve")
    public ResponseEntity<ApiResponse<MemberSignupResponse>> approve(
            @PathVariable Long signupId) {
        return ResponseEntity.ok(ApiResponse.ok(memberSignupService.approve(signupId)));
    }

    // 가입 거절
    @PatchMapping("/{signupId}/reject")
    public ResponseEntity<ApiResponse<MemberSignupResponse>> reject(
            @PathVariable Long signupId) {
        return ResponseEntity.ok(ApiResponse.ok(memberSignupService.reject(signupId)));
    }

    // 가입 신청 취소
    @DeleteMapping("/{signupId}")
    public ResponseEntity<ApiResponse<Void>> cancel(
            @PathVariable Long signupId,
            @AuthenticationPrincipal String userId) {
        memberSignupService.cancel(signupId, userId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}