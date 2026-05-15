package com.crewise.backend.domain.vote.controller;

import com.crewise.backend.domain.vote.dto.VoteCreateRequest;
import com.crewise.backend.domain.vote.dto.VoteHistoryRequest;
import com.crewise.backend.domain.vote.dto.VoteResponse;
import com.crewise.backend.domain.vote.service.VoteService;
import com.crewise.backend.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    // 투표 목록 조회 (팀 멤버만)
    @GetMapping
    public ResponseEntity<ApiResponse<List<VoteResponse>>> getVotes(
            @RequestParam String teamId,
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(voteService.getVotes(teamId, userId)));
    }

    // 투표 상세 조회 (팀 멤버만)
    @GetMapping("/{voteId}")
    public ResponseEntity<ApiResponse<VoteResponse>> getVote(
            @PathVariable Long voteId,
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(voteService.getVote(voteId, userId)));
    }

    // 투표 생성 (모임장만)
    @PostMapping
    public ResponseEntity<ApiResponse<VoteResponse>> createVote(
            @Valid @RequestBody VoteCreateRequest request,
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(voteService.createVote(request, userId)));
    }

    // 투표하기
    @PostMapping("/do")
    public ResponseEntity<ApiResponse<Void>> doVote(
            @Valid @RequestBody VoteHistoryRequest request,
            @AuthenticationPrincipal String userId) {
        voteService.doVote(request, userId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    // 투표 삭제 (모임장만)
    @DeleteMapping("/{voteId}")
    public ResponseEntity<ApiResponse<Void>> deleteVote(
            @PathVariable Long voteId,
            @AuthenticationPrincipal String userId) {
        voteService.deleteVote(voteId, userId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}