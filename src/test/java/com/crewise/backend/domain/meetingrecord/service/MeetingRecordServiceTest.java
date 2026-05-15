package com.crewise.backend.domain.meetingrecord.service;

import com.crewise.backend.domain.meetingrecord.dto.MeetingRecordCreateRequest;
import com.crewise.backend.domain.meetingrecord.entity.MeetingRecord;
import com.crewise.backend.domain.meetingrecord.entity.RecFile;
import com.crewise.backend.domain.meetingrecord.repository.MeetingRecordRepository;
import com.crewise.backend.domain.meetingrecord.repository.RecFileRepository;
import com.crewise.backend.domain.member.entity.Member;
import com.crewise.backend.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MeetingRecordServiceTest {

        @InjectMocks
        private MeetingRecordService meetingRecordService;

        @Mock
        private MeetingRecordRepository meetingRecordRepository;

        @Mock
        private RecFileRepository recFileRepository;

        @Mock
        private MemberRepository memberRepository;

        @Mock
        private RestTemplate restTemplate;

        @Test
        @DisplayName("회의록 목록 조회 성공 - 팀 멤버")
        void getMeetingRecords_success() {
                when(memberRepository.existsByUserIdAndTeamId(any(), any())).thenReturn(true);
                when(meetingRecordRepository.findByTeamIdOrderByMeetingIdDesc(any())).thenReturn(List.of());

                assertDoesNotThrow(() -> meetingRecordService.getMeetingRecords("30MNKA", "userId"));
        }

        @Test
        @DisplayName("회의록 목록 조회 실패 - 비멤버")
        void getMeetingRecords_fail_notMember() {
                when(memberRepository.existsByUserIdAndTeamId(any(), any())).thenReturn(false);

                assertThrows(IllegalArgumentException.class,
                                () -> meetingRecordService.getMeetingRecords("30MNKA", "userId"));
        }

        @Test
        @DisplayName("회의록 생성 성공 - 모임장")
        void createMeetingRecord_success_leader() {
                MeetingRecordCreateRequest request = MeetingRecordCreateRequest.builder()
                                .teamId("30MNKA")
                                .recFileKey("s3://crewise/audio/test.mp3")
                                .build();

                Member mockMember = mock(Member.class);
                when(mockMember.getMemRole()).thenReturn("L");
                when(memberRepository.findByUserIdAndTeamId(any(), any()))
                                .thenReturn(Optional.of(mockMember));

                Map<String, Object> aiResponse = Map.of(
                                "title", "테스트 회의록",
                                "full_script", "전체 스크립트",
                                "summary", "AI 요약");
                when(restTemplate.postForObject(any(String.class), any(), any()))
                                .thenReturn(aiResponse);

                MeetingRecord mockRecord = mock(MeetingRecord.class);
                when(mockRecord.getMeetingId()).thenReturn(1L);
                when(meetingRecordRepository.save(any())).thenReturn(mockRecord);
                when(recFileRepository.save(any())).thenReturn(mock(RecFile.class));

                assertDoesNotThrow(() -> meetingRecordService.createMeetingRecord(request, "userId"));
        }

        @Test
        @DisplayName("회의록 생성 실패 - 일반 멤버")
        void createMeetingRecord_fail_notLeader() {
                MeetingRecordCreateRequest request = MeetingRecordCreateRequest.builder()
                                .teamId("30MNKA")
                                .recFileKey("s3://crewise/audio/test.mp3")
                                .build();

                Member mockMember = mock(Member.class);
                when(mockMember.getMemRole()).thenReturn("M");
                when(memberRepository.findByUserIdAndTeamId(any(), any()))
                                .thenReturn(Optional.of(mockMember));

                assertThrows(IllegalArgumentException.class,
                                () -> meetingRecordService.createMeetingRecord(request, "userId"));
        }

        @Test
        @DisplayName("회의록 삭제 성공 - 모임장")
        void deleteMeetingRecord_success_leader() {
                MeetingRecord mockRecord = mock(MeetingRecord.class);
                when(mockRecord.getTeamId()).thenReturn("30MNKA");
                when(meetingRecordRepository.findById(any())).thenReturn(Optional.of(mockRecord));

                Member mockMember = mock(Member.class);
                when(mockMember.getMemRole()).thenReturn("L");
                when(memberRepository.findByUserIdAndTeamId(any(), any()))
                                .thenReturn(Optional.of(mockMember));

                when(recFileRepository.findByMeetingId(any())).thenReturn(Optional.empty());

                assertDoesNotThrow(() -> meetingRecordService.deleteMeetingRecord(1L, "userId"));
                verify(meetingRecordRepository, times(1)).delete(any());
        }

        @Test
        @DisplayName("회의록 삭제 실패 - 일반 멤버")
        void deleteMeetingRecord_fail_notLeader() {
                MeetingRecord mockRecord = mock(MeetingRecord.class);
                when(mockRecord.getTeamId()).thenReturn("30MNKA");
                when(meetingRecordRepository.findById(any())).thenReturn(Optional.of(mockRecord));

                Member mockMember = mock(Member.class);
                when(mockMember.getMemRole()).thenReturn("M");
                when(memberRepository.findByUserIdAndTeamId(any(), any()))
                                .thenReturn(Optional.of(mockMember));

                assertThrows(IllegalArgumentException.class,
                                () -> meetingRecordService.deleteMeetingRecord(1L, "userId"));
                verify(meetingRecordRepository, never()).delete(any());
        }
}