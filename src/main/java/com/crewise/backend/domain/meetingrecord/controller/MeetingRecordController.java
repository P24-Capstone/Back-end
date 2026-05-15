package com.crewise.backend.domain.meetingrecord.controller;

import com.crewise.backend.domain.meetingrecord.dto.MeetingRecordCreateRequest;
import com.crewise.backend.domain.meetingrecord.dto.MeetingRecordResponse;
import com.crewise.backend.domain.meetingrecord.service.MeetingRecordService;
import com.crewise.backend.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meeting-records")
@RequiredArgsConstructor
public class MeetingRecordController {

    private final MeetingRecordService meetingRecordService;

    // 회의록 목록 조회 (팀 멤버만)
    @GetMapping
    public ResponseEntity<ApiResponse<List<MeetingRecordResponse>>> getMeetingRecords(
            @RequestParam String teamId,
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(meetingRecordService.getMeetingRecords(teamId, userId)));
    }

    // 회의록 상세 조회 (팀 멤버만)
    @GetMapping("/{meetingId}")
    public ResponseEntity<ApiResponse<MeetingRecordResponse>> getMeetingRecord(
            @PathVariable Long meetingId,
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(meetingRecordService.getMeetingRecord(meetingId, userId)));
    }

    // 회의록 생성 (모임장만)
    @PostMapping
    public ResponseEntity<ApiResponse<MeetingRecordResponse>> createMeetingRecord(
            @Valid @RequestBody MeetingRecordCreateRequest request,
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(meetingRecordService.createMeetingRecord(request, userId)));
    }

    // 회의록 삭제 (모임장만)
    @DeleteMapping("/{meetingId}")
    public ResponseEntity<ApiResponse<Void>> deleteMeetingRecord(
            @PathVariable Long meetingId,
            @AuthenticationPrincipal String userId) {
        meetingRecordService.deleteMeetingRecord(meetingId, userId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}