package com.crewise.backend.domain.event.controller;

import com.crewise.backend.domain.event.dto.EventCreateRequest;
import com.crewise.backend.domain.event.dto.EventResponse;
import com.crewise.backend.domain.event.service.EventService;
import com.crewise.backend.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    // 일정 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<EventResponse>>> getEvents(
            @RequestParam String teamId) {
        return ResponseEntity.ok(ApiResponse.ok(eventService.getEvents(teamId)));
    }

    // 일정 상세 조회
    @GetMapping("/{evtId}")
    public ResponseEntity<ApiResponse<EventResponse>> getEvent(
            @PathVariable Long evtId) {
        return ResponseEntity.ok(ApiResponse.ok(eventService.getEvent(evtId)));
    }

    // 일정 등록
    @PostMapping
    public ResponseEntity<ApiResponse<EventResponse>> createEvent(
            @Valid @RequestBody EventCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(eventService.createEvent(request)));
    }

    // 일정 삭제
    @DeleteMapping("/{evtId}")
    public ResponseEntity<ApiResponse<Void>> deleteEvent(
            @PathVariable Long evtId) {
        eventService.deleteEvent(evtId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}