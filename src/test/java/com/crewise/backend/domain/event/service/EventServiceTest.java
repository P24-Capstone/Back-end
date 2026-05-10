package com.crewise.backend.domain.event.service;

import com.crewise.backend.domain.event.dto.EventCreateRequest;
import com.crewise.backend.domain.event.entity.Event;
import com.crewise.backend.domain.event.repository.EventRepository;
import com.crewise.backend.domain.member.entity.Member;
import com.crewise.backend.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @InjectMocks
    private EventService eventService;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private MemberRepository memberRepository;

    @Test
    @DisplayName("일정 목록 조회 성공 - 팀 멤버")
    void getEvents_success() {
        when(memberRepository.existsByUserIdAndTeamId(any(), any())).thenReturn(true);
        when(eventRepository.findByTeamIdOrderByEvtStartDtAsc(any())).thenReturn(List.of());

        assertDoesNotThrow(() -> eventService.getEvents("30MNKA", "userId"));
    }

    @Test
    @DisplayName("일정 목록 조회 실패 - 비멤버")
    void getEvents_fail_notMember() {
        when(memberRepository.existsByUserIdAndTeamId(any(), any())).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> eventService.getEvents("30MNKA", "userId"));
    }

    @Test
    @DisplayName("일정 등록 성공 - 모임장")
    void createEvent_success_leader() {
        EventCreateRequest request = EventCreateRequest.builder()
                .evtTitle("테스트 일정")
                .evtContent("일정 내용")
                .evtStartDt("2026-05-10")
                .evtEndDt("2026-05-10")
                .teamId("30MNKA")
                .build();

        Member mockMember = mock(Member.class);
        when(mockMember.getMemRole()).thenReturn("L");
        when(memberRepository.findByUserIdAndTeamId(any(), any()))
                .thenReturn(Optional.of(mockMember));
        when(eventRepository.save(any())).thenReturn(mock(Event.class));

        assertDoesNotThrow(() -> eventService.createEvent(request, "userId"));
    }

    @Test
    @DisplayName("일정 등록 실패 - 일반 멤버")
    void createEvent_fail_notLeader() {
        EventCreateRequest request = EventCreateRequest.builder()
                .evtTitle("테스트 일정")
                .evtContent("일정 내용")
                .evtStartDt("2026-05-10")
                .evtEndDt("2026-05-10")
                .teamId("30MNKA")
                .build();

        Member mockMember = mock(Member.class);
        when(mockMember.getMemRole()).thenReturn("M");
        when(memberRepository.findByUserIdAndTeamId(any(), any()))
                .thenReturn(Optional.of(mockMember));

        assertThrows(IllegalArgumentException.class,
                () -> eventService.createEvent(request, "userId"));
    }

    @Test
    @DisplayName("일정 삭제 성공 - 모임장")
    void deleteEvent_success_leader() {
        Event mockEvent = mock(Event.class);
        when(mockEvent.getTeamId()).thenReturn("30MNKA");
        when(eventRepository.findById(any())).thenReturn(Optional.of(mockEvent));

        Member mockMember = mock(Member.class);
        when(mockMember.getMemRole()).thenReturn("L");
        when(memberRepository.findByUserIdAndTeamId(any(), any()))
                .thenReturn(Optional.of(mockMember));

        assertDoesNotThrow(() -> eventService.deleteEvent(1L, "userId"));
        verify(eventRepository, times(1)).delete(any());
    }

    @Test
    @DisplayName("일정 삭제 실패 - 일반 멤버")
    void deleteEvent_fail_notLeader() {
        Event mockEvent = mock(Event.class);
        when(mockEvent.getTeamId()).thenReturn("30MNKA");
        when(eventRepository.findById(any())).thenReturn(Optional.of(mockEvent));

        Member mockMember = mock(Member.class);
        when(mockMember.getMemRole()).thenReturn("M");
        when(memberRepository.findByUserIdAndTeamId(any(), any()))
                .thenReturn(Optional.of(mockMember));

        assertThrows(IllegalArgumentException.class,
                () -> eventService.deleteEvent(1L, "userId"));
        verify(eventRepository, never()).delete(any());
    }
}