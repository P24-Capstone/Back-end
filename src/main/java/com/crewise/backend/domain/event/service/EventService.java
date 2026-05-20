package com.crewise.backend.domain.event.service;

import com.crewise.backend.domain.event.dto.EventCreateRequest;
import com.crewise.backend.domain.event.dto.EventResponse;
import com.crewise.backend.domain.event.entity.Event;
import com.crewise.backend.domain.event.repository.EventRepository;
import com.crewise.backend.domain.member.entity.Member;
import com.crewise.backend.domain.member.repository.MemberRepository;
import com.crewise.backend.domain.news.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final MemberRepository memberRepository;
    private final NewsService newsService;

    // 팀 멤버 여부 확인
    private void checkTeamMember(String userId, String teamId) {
        if (!memberRepository.existsByUserIdAndTeamId(userId, teamId)) {
            throw new IllegalArgumentException("해당 모임의 멤버가 아닙니다.");
        }
    }

    // 모임장 여부 확인
    private void checkLeader(String userId, String teamId) {
        Member member = memberRepository.findByUserIdAndTeamId(userId, teamId)
                .orElseThrow(() -> new IllegalArgumentException("해당 모임의 멤버가 아닙니다."));
        if (!"L".equals(member.getMemRole())) {
            throw new IllegalArgumentException("모임장만 가능한 작업입니다.");
        }
    }

    // 일정 목록 조회 (팀 멤버만)
    @Transactional(readOnly = true)
    public List<EventResponse> getEvents(String teamId, String userId) {
        checkTeamMember(userId, teamId);
        return eventRepository.findByTeamIdOrderByEvtStartDtAsc(teamId)
                .stream()
                .map(EventResponse::from)
                .collect(Collectors.toList());
    }

    // 일정 상세 조회 (팀 멤버만)
    @Transactional(readOnly = true)
    public EventResponse getEvent(Long evtId, String userId) {
        Event event = eventRepository.findById(evtId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 일정입니다."));
        checkTeamMember(userId, event.getTeamId());
        return EventResponse.from(event);
    }

    // 일정 등록 (모임장만)
    @Transactional
    public EventResponse createEvent(EventCreateRequest request, String userId) {
        checkLeader(userId, request.getTeamId());
        Event event = Event.builder()
                .evtTitle(request.getEvtTitle())
                .evtContent(request.getEvtContent())
                .evtStartDt(request.getEvtStartDt())
                .evtEndDt(request.getEvtEndDt())
                .evtRegDtm(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .teamId(request.getTeamId())
                .build();
        Event savedEvent = eventRepository.save(event);
        newsService.createNews("E", savedEvent.getEvtId(),
                "📆 새 일정이 등록됐어요! " + savedEvent.getEvtTitle(), savedEvent.getTeamId());
        return EventResponse.from(savedEvent);
    }

    // 일정 수정 (모임장만)
    @Transactional
    public EventResponse updateEvent(Long evtId, EventCreateRequest request, String userId) {
        Event event = eventRepository.findById(evtId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 일정입니다."));
        checkLeader(userId, event.getTeamId());

        Event updated = Event.builder()
                .evtId(event.getEvtId())
                .evtTitle(request.getEvtTitle())
                .evtContent(request.getEvtContent())
                .evtStartDt(request.getEvtStartDt())
                .evtEndDt(request.getEvtEndDt())
                .evtRegDtm(event.getEvtRegDtm())
                .teamId(event.getTeamId())
                .build();

        return EventResponse.from(eventRepository.save(updated));
    }

    // 일정 삭제 (모임장만)
    @Transactional
    public void deleteEvent(Long evtId, String userId) {
        Event event = eventRepository.findById(evtId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 일정입니다."));
        checkLeader(userId, event.getTeamId());
        eventRepository.delete(event);
    }
}