package com.crewise.backend.domain.news.controller;

import com.crewise.backend.domain.news.dto.CommentCreateRequest;
import com.crewise.backend.domain.news.dto.CommentResponse;
import com.crewise.backend.domain.news.dto.NewsResponse;
import com.crewise.backend.domain.news.service.NewsService;
import com.crewise.backend.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    // 최근 소식 목록 조회 (팀 멤버만)
    @GetMapping
    public ResponseEntity<ApiResponse<List<NewsResponse>>> getNewsList(
            @RequestParam String teamId,
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(newsService.getNewsList(teamId, userId)));
    }

    // 댓글 목록 조회 (팀 멤버만)
    @GetMapping("/{newsId}/comments")
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getComments(
            @PathVariable Long newsId,
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(newsService.getComments(newsId, userId)));
    }

    // 댓글 작성 (팀 멤버만)
    @PostMapping("/comments")
    public ResponseEntity<ApiResponse<CommentResponse>> createComment(
            @Valid @RequestBody CommentCreateRequest request,
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(newsService.createComment(request, userId)));
    }

    // 댓글 수정 (본인만)
    @PutMapping("/comments/{cmtId}")
    public ResponseEntity<ApiResponse<CommentResponse>> updateComment(
            @PathVariable Long cmtId,
            @RequestParam String cmtContent,
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(newsService.updateComment(cmtId, cmtContent, userId)));
    }

    // 댓글 삭제 (본인만)
    @DeleteMapping("/comments/{cmtId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long cmtId,
            @AuthenticationPrincipal String userId) {
        newsService.deleteComment(cmtId, userId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}