package com.crewise.backend.domain.notice.service;

import com.crewise.backend.domain.member.entity.Member;
import com.crewise.backend.domain.member.repository.MemberRepository;
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
    private final MemberRepository memberRepository;

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

    // 공지 목록 조회 (팀 멤버만)
    @Transactional(readOnly = true)
    public List<NoticeResponse> getNotices(String teamId, String userId) {
        checkTeamMember(userId, teamId);
        return noticeRepository.findByTeamIdOrderByNotiIdDesc(teamId)
                .stream()
                .map(NoticeResponse::from)
                .collect(Collectors.toList());
    }

    // 공지 상세 조회 (팀 멤버만)
    @Transactional(readOnly = true)
    public NoticeResponse getNotice(Long notiId, String userId) {
        Notice notice = noticeRepository.findById(notiId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공지입니다."));
        checkTeamMember(userId, notice.getTeamId());
        return NoticeResponse.from(notice);
    }

    // 공지 등록 (모임장만)
    @Transactional
    public NoticeResponse createNotice(NoticeCreateRequest request, String userId) {
        checkLeader(userId, request.getTeamId());
        Notice notice = Notice.builder()
                .notiTitle(request.getNotiTitle())
                .notiContent(request.getNotiContent())
                .notiFix(request.getNotiFix() != null ? request.getNotiFix() : "N")
                .regDtm(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .teamId(request.getTeamId())
                .build();
        return NoticeResponse.from(noticeRepository.save(notice));
    }

    // 공지 삭제 (모임장만)
    @Transactional
    public void deleteNotice(Long notiId, String userId) {
        Notice notice = noticeRepository.findById(notiId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공지입니다."));
        checkLeader(userId, notice.getTeamId());
        noticeRepository.delete(notice);
    }

    // 공지 수정 (모임장만)
    @Transactional
    public NoticeResponse updateNotice(Long notiId, NoticeCreateRequest request, String userId) {
        Notice notice = noticeRepository.findById(notiId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공지입니다."));
        checkLeader(userId, notice.getTeamId());

        Notice updated = Notice.builder()
                .notiId(notice.getNotiId())
                .notiTitle(request.getNotiTitle())
                .notiContent(request.getNotiContent())
                .notiFix(request.getNotiFix() != null ? request.getNotiFix() : notice.getNotiFix())
                .regDtm(notice.getRegDtm())
                .modDtm(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .teamId(notice.getTeamId())
                .build();
        return NoticeResponse.from(noticeRepository.save(updated));
    }
}