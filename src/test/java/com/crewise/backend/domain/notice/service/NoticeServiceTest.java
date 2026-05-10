package com.crewise.backend.domain.notice.service;

import com.crewise.backend.domain.member.entity.Member;
import com.crewise.backend.domain.member.repository.MemberRepository;
import com.crewise.backend.domain.notice.dto.NoticeCreateRequest;
import com.crewise.backend.domain.notice.entity.Notice;
import com.crewise.backend.domain.notice.repository.NoticeRepository;
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
class NoticeServiceTest {

    @InjectMocks
    private NoticeService noticeService;

    @Mock
    private NoticeRepository noticeRepository;

    @Mock
    private MemberRepository memberRepository;

    @Test
    @DisplayName("공지 목록 조회 성공 - 팀 멤버")
    void getNotices_success() {
        // given
        when(memberRepository.existsByUserIdAndTeamId(any(), any())).thenReturn(true);
        when(noticeRepository.findByTeamIdOrderByNotiIdDesc(any())).thenReturn(List.of());

        // when & then
        assertDoesNotThrow(() -> noticeService.getNotices("30MNKA", "userId"));
    }

    @Test
    @DisplayName("공지 목록 조회 실패 - 비멤버")
    void getNotices_fail_notMember() {
        // given
        when(memberRepository.existsByUserIdAndTeamId(any(), any())).thenReturn(false);

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> noticeService.getNotices("30MNKA", "userId"));
    }

    @Test
    @DisplayName("공지 등록 성공 - 모임장")
    void createNotice_success_leader() {
        // given
        NoticeCreateRequest request = NoticeCreateRequest.builder()
                .notiTitle("테스트 공지")
                .notiContent("공지 내용")
                .teamId("30MNKA")
                .build();

        Member mockMember = mock(Member.class);
        when(mockMember.getMemRole()).thenReturn("L");
        when(memberRepository.findByUserIdAndTeamId(any(), any()))
                .thenReturn(Optional.of(mockMember));
        when(noticeRepository.save(any())).thenReturn(mock(Notice.class));

        // when & then
        assertDoesNotThrow(() -> noticeService.createNotice(request, "userId"));
    }

    @Test
    @DisplayName("공지 등록 실패 - 일반 멤버")
    void createNotice_fail_notLeader() {
        // given
        NoticeCreateRequest request = NoticeCreateRequest.builder()
                .notiTitle("테스트 공지")
                .notiContent("공지 내용")
                .teamId("30MNKA")
                .build();

        Member mockMember = mock(Member.class);
        when(mockMember.getMemRole()).thenReturn("M");
        when(memberRepository.findByUserIdAndTeamId(any(), any()))
                .thenReturn(Optional.of(mockMember));

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> noticeService.createNotice(request, "userId"));
    }

    @Test
    @DisplayName("공지 삭제 성공 - 모임장")
    void deleteNotice_success_leader() {
        // given
        Notice mockNotice = mock(Notice.class);
        when(mockNotice.getTeamId()).thenReturn("30MNKA");
        when(noticeRepository.findById(any())).thenReturn(Optional.of(mockNotice));

        Member mockMember = mock(Member.class);
        when(mockMember.getMemRole()).thenReturn("L");
        when(memberRepository.findByUserIdAndTeamId(any(), any()))
                .thenReturn(Optional.of(mockMember));

        // when & then
        assertDoesNotThrow(() -> noticeService.deleteNotice(1L, "userId"));
        verify(noticeRepository, times(1)).delete(any());
    }

    @Test
    @DisplayName("공지 삭제 실패 - 일반 멤버")
    void deleteNotice_fail_notLeader() {
        // given
        Notice mockNotice = mock(Notice.class);
        when(mockNotice.getTeamId()).thenReturn("30MNKA");
        when(noticeRepository.findById(any())).thenReturn(Optional.of(mockNotice));

        Member mockMember = mock(Member.class);
        when(mockMember.getMemRole()).thenReturn("M");
        when(memberRepository.findByUserIdAndTeamId(any(), any()))
                .thenReturn(Optional.of(mockMember));

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> noticeService.deleteNotice(1L, "userId"));
        verify(noticeRepository, never()).delete(any());
    }
}