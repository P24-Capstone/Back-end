package com.crewise.backend.domain.member.controller;

import com.crewise.backend.domain.member.dto.MemberCreateRequest;
import com.crewise.backend.domain.member.dto.MemberJoinByCodeRequest;
import com.crewise.backend.domain.member.dto.MemberResponse;
import com.crewise.backend.domain.member.dto.MemberUpdateRequest;
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

    // 모임원 목록 조회 (리더: 전체 상태, 일반: A 상태만)
    @GetMapping
    public ResponseEntity<ApiResponse<List<MemberResponse>>> getMembers(
            @RequestParam String teamId,
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(memberService.getMembers(teamId, userId)));
    }

    // 내 멤버 정보 조회
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MemberResponse>> getMyMembership(
            @RequestParam String teamId,
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(memberService.getMyMembership(teamId, userId)));
    }

    // 모임원 상세 조회
    @GetMapping("/{memId}")
    public ResponseEntity<ApiResponse<MemberResponse>> getMember(
            @PathVariable String memId) {
        return ResponseEntity.ok(ApiResponse.ok(memberService.getMember(memId)));
    }

    // 가입 대기 목록 조회 (모임장만)
    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<MemberResponse>>> getPendingMembers(
            @RequestParam String teamId,
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(memberService.getPendingMembers(teamId, userId)));
    }

    // 모임 가입
    @PostMapping("/join")
    public ResponseEntity<ApiResponse<MemberResponse>> joinTeam(
            @Valid @RequestBody MemberCreateRequest request,
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(memberService.joinTeam(request, userId)));
    }

    // 추천코드로 모임 가입
    @PostMapping("/join/code")
    public ResponseEntity<ApiResponse<MemberResponse>> joinTeamByCode(
            @Valid @RequestBody MemberJoinByCodeRequest request,
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(memberService.joinTeamByCode(request, userId)));
    }

    // 모임원 정보 수정 (본인만)
    @PatchMapping("/{memId}")
    public ResponseEntity<ApiResponse<MemberResponse>> updateMember(
            @PathVariable String memId,
            @RequestBody MemberUpdateRequest request,
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(memberService.updateMember(memId, request, userId)));
    }

    // 모임 탈퇴
    @DeleteMapping("/{memId}")
    public ResponseEntity<ApiResponse<Void>> leaveTeam(
            @PathVariable String memId,
            @AuthenticationPrincipal String userId) {
        memberService.leaveTeam(memId, userId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    // 가입 승인 (모임장만)
    @PatchMapping("/{memId}/approve")
    public ResponseEntity<ApiResponse<MemberResponse>> approveMember(
            @PathVariable String memId,
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(memberService.approveMember(memId, userId)));
    }

    // 가입 거절 (모임장만)
    @PatchMapping("/{memId}/reject")
    public ResponseEntity<ApiResponse<MemberResponse>> rejectMember(
            @PathVariable String memId,
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(memberService.rejectMember(memId, userId)));
    }
}