package com.crewise.backend.domain.event.controller;

import com.crewise.backend.domain.event.dto.EventCreateRequest;
import com.crewise.backend.domain.event.dto.EventResponse;
import com.crewise.backend.domain.event.service.EventService;
import com.crewise.backend.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    // 일정 목록 조회 (팀 멤버만)
    @GetMapping
    public ResponseEntity<ApiResponse<List<EventResponse>>> getEvents(
            @RequestParam String teamId,
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(eventService.getEvents(teamId, userId)));
    }

    // 일정 상세 조회 (팀 멤버만)
    @GetMapping("/{evtId}")
    public ResponseEntity<ApiResponse<EventResponse>> getEvent(
            @PathVariable Long evtId,
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(eventService.getEvent(evtId, userId)));
    }

    // 일정 등록 (모임장만)
    @PostMapping
    public ResponseEntity<ApiResponse<EventResponse>> createEvent(
            @Valid @RequestBody EventCreateRequest request,
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(eventService.createEvent(request, userId)));
    }

    // 일정 수정 (모임장만)
    @PutMapping("/{evtId}")
    public ResponseEntity<ApiResponse<EventResponse>> updateEvent(
            @PathVariable Long evtId,
            @Valid @RequestBody EventCreateRequest request,
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(eventService.updateEvent(evtId, request, userId)));
    }

    // 일정 삭제 (모임장만)
    @DeleteMapping("/{evtId}")
    public ResponseEntity<ApiResponse<Void>> deleteEvent(
            @PathVariable Long evtId,
            @AuthenticationPrincipal String userId) {
        eventService.deleteEvent(evtId, userId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}