package com.crewise.backend.domain.notice.service;

import com.crewise.backend.domain.notice.dto.NoticeCreateRequest;
import com.crewise.backend.domain.notice.dto.NoticeResponse;
import com.crewise.backend.domain.notice.entity.Notice;
import com.crewise.backend.domain.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    // 공지 목록 조회
    @Transactional(readOnly = true)
    public List<NoticeResponse> getNotices(String teamId) {
        return noticeRepository.findByTeamIdOrderByNotiIdDesc(teamId)
                .stream()
                .map(NoticeResponse::from)
                .collect(Collectors.toList());
    }

    // 공지 상세 조회
    @Transactional(readOnly = true)
    public NoticeResponse getNotice(Long notiId) {
        Notice notice = noticeRepository.findById(notiId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공지입니다."));
        return NoticeResponse.from(notice);
    }

    // 공지 등록
    @Transactional
    public NoticeResponse createNotice(NoticeCreateRequest request) {
        Notice notice = Notice.builder()
                .notiTitle(request.getNotiTitle())
                .notiContent(request.getNotiContent())
                .notiFix(request.getNotiFix() != null ? request.getNotiFix() : "N")
                .regDtm(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .teamId(request.getTeamId())
                .build();

        return NoticeResponse.from(noticeRepository.save(notice));
    }

    // 공지 삭제
    @Transactional
    public void deleteNotice(Long notiId) {
        Notice notice = noticeRepository.findById(notiId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공지입니다."));
        noticeRepository.delete(notice);
    }
}