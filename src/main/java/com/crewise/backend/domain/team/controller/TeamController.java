package com.crewise.backend.domain.team.controller;

import com.crewise.backend.domain.team.dto.TeamCreateRequest;
import com.crewise.backend.domain.team.dto.TeamResponse;
import com.crewise.backend.domain.team.service.TeamService;
import com.crewise.backend.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    // 모임 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<TeamResponse>>> getTeams(
            @RequestParam(required = false) String teamName) {
        return ResponseEntity.ok(ApiResponse.ok(teamService.getTeams(teamName)));
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
            @Valid @RequestBody TeamCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(teamService.createTeam(request)));
    }
}