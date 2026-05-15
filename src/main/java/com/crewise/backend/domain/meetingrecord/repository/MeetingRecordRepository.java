package com.crewise.backend.domain.meetingrecord.repository;

import com.crewise.backend.domain.meetingrecord.entity.MeetingRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeetingRecordRepository extends JpaRepository<MeetingRecord, Long> {
    List<MeetingRecord> findByTeamIdOrderByMeetingIdDesc(String teamId);
}