package com.crewise.backend.domain.notice.repository;

import com.crewise.backend.domain.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findByTeamIdOrderByNotiIdDesc(String teamId);
}