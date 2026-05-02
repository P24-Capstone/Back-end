package com.crewise.backend.domain.notice.controller;

import com.crewise.backend.domain.notice.dto.NoticeCreateRequest;
import com.crewise.backend.domain.notice.dto.NoticeResponse;
import com.crewise.backend.domain.notice.service.NoticeService;
import com.crewise.backend.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    // 공지 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<NoticeResponse>>> getNotices(
            @RequestParam String teamId) {
        return ResponseEntity.ok(ApiResponse.ok(noticeService.getNotices(teamId)));
    }

    // 공지 상세 조회
    @GetMapping("/{noticeId}")
    public ResponseEntity<ApiResponse<NoticeResponse>> getNotice(
            @PathVariable Long noticeId) {
        return ResponseEntity.ok(ApiResponse.ok(noticeService.getNotice(noticeId)));
    }

    // 공지 등록
    @PostMapping
    public ResponseEntity<ApiResponse<NoticeResponse>> createNotice(
            @Valid @RequestBody NoticeCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(noticeService.createNotice(request)));
    }

    // 공지 삭제
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<ApiResponse<Void>> deleteNotice(
            @PathVariable Long noticeId) {
        noticeService.deleteNotice(noticeId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}