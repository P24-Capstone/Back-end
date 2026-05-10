package com.crewise.backend.domain.event.repository;

import com.crewise.backend.domain.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByTeamIdOrderByEvtStartDtAsc(String teamId);
}