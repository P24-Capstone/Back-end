package com.crewise.backend.domain.member.controller;

import com.crewise.backend.domain.member.dto.MemberCreateRequest;
import com.crewise.backend.domain.member.dto.MemberResponse;
import com.crewise.backend.domain.member.service.MemberService;
import com.crewise.backend.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 모임원 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<MemberResponse>>> getMembers(
            @RequestParam String teamId) {
        return ResponseEntity.ok(ApiResponse.ok(memberService.getMembers(teamId)));
    }

    // 모임원 상세 조회
    @GetMapping("/{memId}")
    public ResponseEntity<ApiResponse<MemberResponse>> getMember(
            @PathVariable String memId) {
        return ResponseEntity.ok(ApiResponse.ok(memberService.getMember(memId)));
    }

    // 모임 가입
    @PostMapping("/join")
    public ResponseEntity<ApiResponse<MemberResponse>> joinTeam(
            @Valid @RequestBody MemberCreateRequest request,
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(memberService.joinTeam(request, userId)));
    }

    // 모임 탈퇴
    @DeleteMapping("/{memId}")
    public ResponseEntity<ApiResponse<Void>> leaveTeam(
            @PathVariable String memId,
            @AuthenticationPrincipal String userId) {
        memberService.leaveTeam(memId, userId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}