package com.crewise.backend.domain.team.controller;

import com.crewise.backend.domain.team.dto.TeamCreateRequest;
import com.crewise.backend.domain.team.dto.TeamResponse;
import com.crewise.backend.domain.team.service.TeamService;
import com.crewise.backend.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    // 내 모임 목록 조회 (가입한 모임만)
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<TeamResponse>>> getMyTeams(
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(teamService.getMyTeams(userId)));
    }

    // 대기 중인 모임 목록 조회 (가입 신청 후 승인 대기)
    @GetMapping("/my/waiting")
    public ResponseEntity<ApiResponse<List<TeamResponse>>> getWaitTeams(
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(teamService.getWaitTeams(userId)));
    }

    // 모임 상세 조회
    @GetMapping("/{teamId}")
    public ResponseEntity<ApiResponse<TeamResponse>> getTeam(
            @PathVariable String teamId) {
        return ResponseEntity.ok(ApiResponse.ok(teamService.getTeam(teamId)));
    }

    // 모임 생성
    @PostMapping
    public ResponseEntity<ApiResponse<TeamResponse>> createTeam(
            @Valid @RequestBody TeamCreateRequest request,
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(teamService.createTeam(request, userId)));
    }

    // 모임 수정 (모임장만)
    @PutMapping("/{teamId}")
    public ResponseEntity<ApiResponse<TeamResponse>> updateTeam(
            @PathVariable String teamId,
            @Valid @RequestBody TeamCreateRequest request,
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(teamService.updateTeam(teamId, request, userId)));
    }

    // 모임 삭제 (모임장만)
    @DeleteMapping("/{teamId}")
    public ResponseEntity<ApiResponse<Void>> deleteTeam(
            @PathVariable String teamId,
            @AuthenticationPrincipal String userId) {
        teamService.deleteTeam(teamId, userId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}