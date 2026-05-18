package com.crewise.backend.domain.mission.controller;

import com.crewise.backend.domain.mission.dto.MissionCreateRequest;
import com.crewise.backend.domain.mission.dto.MissionResponse;
import com.crewise.backend.domain.mission.dto.MissionVerifyRequest;
import com.crewise.backend.domain.mission.dto.MissionVerifyResponse;
import com.crewise.backend.domain.mission.dto.MissionVerifyUpdateRequest;
import com.crewise.backend.domain.mission.entity.MissionVerify;
import com.crewise.backend.domain.mission.service.MissionService;
import com.crewise.backend.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/missions")
@RequiredArgsConstructor
public class MissionController {

    private final MissionService missionService;

    // 미션 목록 조회 (팀 멤버만)
    @GetMapping
    public ResponseEntity<ApiResponse<List<MissionResponse>>> getMissions(
            @RequestParam String teamId,
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(missionService.getMissions(teamId, userId)));
    }

    // 미션 상세 조회 (팀 멤버만)
    @GetMapping("/{missionId}")
    public ResponseEntity<ApiResponse<MissionResponse>> getMission(
            @PathVariable Long missionId,
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(missionService.getMission(missionId, userId)));
    }

    // 미션 생성 (모임장만)
    @PostMapping
    public ResponseEntity<ApiResponse<MissionResponse>> createMission(
            @Valid @RequestBody MissionCreateRequest request,
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(missionService.createMission(request, userId)));
    }

    // 미션 삭제 (모임장만)
    @DeleteMapping("/{missionId}")
    public ResponseEntity<ApiResponse<Void>> deleteMission(
            @PathVariable Long missionId,
            @AuthenticationPrincipal String userId) {
        missionService.deleteMission(missionId, userId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    // 미션 인증 (FastAPI 호출)
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<MissionVerify>> verifyMission(
            @Valid @RequestBody MissionVerifyRequest request,
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(missionService.verifyMission(request, userId)));
    }

    // 제출 목록 조회 (모임장만)
    @GetMapping("/{missionId}/submissions")
    public ResponseEntity<ApiResponse<List<MissionVerifyResponse>>> getSubmissions(
            @PathVariable Long missionId,
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(missionService.getSubmissions(missionId, userId)));
    }

    // 내 제출 현황 조회
    @GetMapping("/{missionId}/submissions/me")
    public ResponseEntity<ApiResponse<MissionVerifyResponse>> getMySubmission(
            @PathVariable Long missionId,
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(missionService.getMySubmission(missionId, userId)));
    }

    // 제출 상세 조회
    @GetMapping("/submissions/{verifyId}")
    public ResponseEntity<ApiResponse<MissionVerifyResponse>> getSubmission(
            @PathVariable Long verifyId,
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(missionService.getSubmission(verifyId, userId)));
    }

    // 승인 (모임장만)
    @PatchMapping("/submissions/{verifyId}/approve")
    public ResponseEntity<ApiResponse<MissionVerifyResponse>> approveSubmission(
            @PathVariable Long verifyId,
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(missionService.approveSubmission(verifyId, userId)));
    }

    // 거절 (모임장만)
    @PatchMapping("/submissions/{verifyId}/reject")
    public ResponseEntity<ApiResponse<MissionVerifyResponse>> rejectSubmission(
            @PathVariable Long verifyId,
            @RequestBody MissionVerifyUpdateRequest request,
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(missionService.rejectSubmission(verifyId, request.getRejectReason(), userId)));
    }
}