package com.crewise.backend.domain.meetingrecord.repository;

import com.crewise.backend.domain.meetingrecord.entity.RecFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecFileRepository extends JpaRepository<RecFile, Long> {
    Optional<RecFile> findByMeetingId(Long meetingId);
}