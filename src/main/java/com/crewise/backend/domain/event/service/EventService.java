package com.crewise.backend.domain.event.service;

import com.crewise.backend.domain.event.dto.EventCreateRequest;
import com.crewise.backend.domain.event.dto.EventResponse;
import com.crewise.backend.domain.event.entity.Event;
import com.crewise.backend.domain.event.repository.EventRepository;
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

    // 일정 목록 조회
    @Transactional(readOnly = true)
    public List<EventResponse> getEvents(String teamId) {
        return eventRepository.findByTeamIdOrderByEvtStartDtAsc(teamId)
                .stream()
                .map(EventResponse::from)
                .collect(Collectors.toList());
    }

    // 일정 상세 조회
    @Transactional(readOnly = true)
    public EventResponse getEvent(Long evtId) {
        Event event = eventRepository.findById(evtId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 일정입니다."));
        return EventResponse.from(event);
    }

    // 일정 등록
    @Transactional
    public EventResponse createEvent(EventCreateRequest request) {
        Event event = Event.builder()
                .evtTitle(request.getEvtTitle())
                .evtContent(request.getEvtContent())
                .evtStartDt(request.getEvtStartDt())
                .evtEndDt(request.getEvtEndDt())
                .evtRegDtm(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .teamId(request.getTeamId())
                .build();

        return EventResponse.from(eventRepository.save(event));
    }

    // 일정 삭제
    @Transactional
    public void deleteEvent(Long evtId) {
        Event event = eventRepository.findById(evtId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 일정입니다."));
        eventRepository.delete(event);
    }
}