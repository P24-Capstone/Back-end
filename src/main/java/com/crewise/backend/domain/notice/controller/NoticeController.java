package com.crewise.backend.domain.notice.controller;

import com.crewise.backend.domain.notice.dto.NoticeCreateRequest;
import com.crewise.backend.domain.notice.dto.NoticeResponse;
import com.crewise.backend.domain.notice.service.NoticeService;
import com.crewise.backend.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    // 공지 목록 조회 (팀 멤버만)
    @GetMapping
    public ResponseEntity<ApiResponse<List<NoticeResponse>>> getNotices(
            @RequestParam String teamId,
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(noticeService.getNotices(teamId, userId)));
    }

    // 공지 상세 조회 (팀 멤버만)
    @GetMapping("/{notiId}")
    public ResponseEntity<ApiResponse<NoticeResponse>> getNotice(
            @PathVariable Long notiId,
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(noticeService.getNotice(notiId, userId)));
    }

    // 공지 등록 (모임장만)
    @PostMapping
    public ResponseEntity<ApiResponse<NoticeResponse>> createNotice(
            @Valid @RequestBody NoticeCreateRequest request,
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(noticeService.createNotice(request, userId)));
    }

    // 공지 수정 (모임장만)
    @PutMapping("/{notiId}")
    public ResponseEntity<ApiResponse<NoticeResponse>> updateNotice(
            @PathVariable Long notiId,
            @Valid @RequestBody NoticeCreateRequest request,
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(noticeService.updateNotice(notiId, request, userId)));
    }

    // 공지 삭제 (모임장만)
    @DeleteMapping("/{notiId}")
    public ResponseEntity<ApiResponse<Void>> deleteNotice(
            @PathVariable Long notiId,
            @AuthenticationPrincipal String userId) {
        noticeService.deleteNotice(notiId, userId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}