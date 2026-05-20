package com.crewise.backend.domain.dashboard.controller;

import com.crewise.backend.domain.dashboard.dto.DashboardLeaderResponse;
import com.crewise.backend.domain.dashboard.dto.DashboardMemberResponse;
import com.crewise.backend.domain.dashboard.service.DashboardService;
import com.crewise.backend.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/member")
    public ResponseEntity<ApiResponse<DashboardMemberResponse>> getMemberDashboard(
            @RequestParam String teamId,
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(dashboardService.getDashboardMember(teamId, userId)));
    }

    @GetMapping("/leader")
    public ResponseEntity<ApiResponse<DashboardLeaderResponse>> getLeaderDashboard(
            @RequestParam String teamId,
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(dashboardService.getDashboardLeader(teamId, userId)));
    }
}
