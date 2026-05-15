package com.crewise.backend.domain.meetingrecord.service;

import com.crewise.backend.domain.meetingrecord.dto.MeetingRecordCreateRequest;
import com.crewise.backend.domain.meetingrecord.dto.MeetingRecordResponse;
import com.crewise.backend.domain.meetingrecord.entity.MeetingRecord;
import com.crewise.backend.domain.meetingrecord.entity.RecFile;
import com.crewise.backend.domain.meetingrecord.repository.MeetingRecordRepository;
import com.crewise.backend.domain.meetingrecord.repository.RecFileRepository;
import com.crewise.backend.domain.member.entity.Member;
import com.crewise.backend.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MeetingRecordService {

    private final MeetingRecordRepository meetingRecordRepository;
    private final RecFileRepository recFileRepository;
    private final MemberRepository memberRepository;
    private final RestTemplate restTemplate;

    @Value("${ai.serving.url}")
    private String aiServingUrl;

    // 팀 멤버 확인
    private void checkTeamMember(String userId, String teamId) {
        if (!memberRepository.existsByUserIdAndTeamId(userId, teamId)) {
            throw new IllegalArgumentException("해당 모임의 멤버가 아닙니다.");
        }
    }

    // 모임장 확인
    private void checkLeader(String userId, String teamId) {
        Member member = memberRepository.findByUserIdAndTeamId(userId, teamId)
                .orElseThrow(() -> new IllegalArgumentException("해당 모임의 멤버가 아닙니다."));
        if (!"L".equals(member.getMemRole())) {
            throw new IllegalArgumentException("모임장만 가능한 작업입니다.");
        }
    }

    // 회의록 목록 조회 (팀 멤버만)
    @Transactional(readOnly = true)
    public List<MeetingRecordResponse> getMeetingRecords(String teamId, String userId) {
        checkTeamMember(userId, teamId);
        return meetingRecordRepository.findByTeamIdOrderByMeetingIdDesc(teamId)
                .stream()
                .map(record -> {
                    String recFileKey = recFileRepository.findByMeetingId(record.getMeetingId())
                            .map(RecFile::getRecFileKey)
                            .orElse(null);
                    return MeetingRecordResponse.from(record, recFileKey);
                })
                .collect(Collectors.toList());
    }

    // 회의록 상세 조회 (팀 멤버만)
    @Transactional(readOnly = true)
    public MeetingRecordResponse getMeetingRecord(Long meetingId, String userId) {
        MeetingRecord record = meetingRecordRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회의록입니다."));
        checkTeamMember(userId, record.getTeamId());

        String recFileKey = recFileRepository.findByMeetingId(meetingId)
                .map(RecFile::getRecFileKey)
                .orElse(null);

        return MeetingRecordResponse.from(record, recFileKey);
    }

    // 회의록 생성 (모임장만) - gcube FastAPI 호출
    @Transactional
    public MeetingRecordResponse createMeetingRecord(MeetingRecordCreateRequest request, String userId) {
        checkLeader(userId, request.getTeamId());

        // gcube FastAPI 호출 (STT + 요약/제목)
        Map<String, Object> body = new HashMap<>();
        body.put("rec_file_key", request.getRecFileKey());

        Map<String, Object> aiResponse = restTemplate.postForObject(
                aiServingUrl + "/meeting-record",
                body,
                Map.class);

        String meetingTitle = (String) aiResponse.get("title");
        String fullScript = (String) aiResponse.get("full_script");
        String aiSummary = (String) aiResponse.get("summary");

        // 회의록 저장
        MeetingRecord record = MeetingRecord.builder()
                .meetingTitle(meetingTitle)
                .fullScript(fullScript)
                .aiSummary(aiSummary)
                .regDtm(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .teamId(request.getTeamId())
                .build();

        MeetingRecord savedRecord = meetingRecordRepository.save(record);

        // 음성 파일 저장
        if (request.getRecFileKey() != null && !request.getRecFileKey().isEmpty()) {
            RecFile recFile = RecFile.builder()
                    .meetingId(savedRecord.getMeetingId())
                    .recFileKey(request.getRecFileKey())
                    .build();
            recFileRepository.save(recFile);
        }

        return MeetingRecordResponse.from(savedRecord, request.getRecFileKey());
    }

    // 회의록 삭제 (모임장만)
    @Transactional
    public void deleteMeetingRecord(Long meetingId, String userId) {
        MeetingRecord record = meetingRecordRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회의록입니다."));
        checkLeader(userId, record.getTeamId());

        recFileRepository.findByMeetingId(meetingId)
                .ifPresent(recFileRepository::delete);

        meetingRecordRepository.delete(record);
    }
}