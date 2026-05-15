package com.crewise.backend.domain.vote.service;

import com.crewise.backend.domain.member.entity.Member;
import com.crewise.backend.domain.member.repository.MemberRepository;
import com.crewise.backend.domain.vote.dto.VoteCreateRequest;
import com.crewise.backend.domain.vote.dto.VoteHistoryRequest;
import com.crewise.backend.domain.vote.entity.Vote;
import com.crewise.backend.domain.vote.entity.VoteHistory;
import com.crewise.backend.domain.vote.entity.VoteOption;
import com.crewise.backend.domain.vote.repository.VoteHistoryRepository;
import com.crewise.backend.domain.vote.repository.VoteOptionRepository;
import com.crewise.backend.domain.vote.repository.VoteRepository;
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
class VoteServiceTest {

    @InjectMocks
    private VoteService voteService;

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private VoteOptionRepository voteOptionRepository;

    @Mock
    private VoteHistoryRepository voteHistoryRepository;

    @Mock
    private MemberRepository memberRepository;

    @Test
    @DisplayName("투표 목록 조회 성공 - 팀 멤버")
    void getVotes_success() {
        when(memberRepository.existsByUserIdAndTeamId(any(), any())).thenReturn(true);
        when(voteRepository.findByTeamIdOrderByVoteIdDesc(any())).thenReturn(List.of());

        assertDoesNotThrow(() -> voteService.getVotes("30MNKA", "userId"));
    }

    @Test
    @DisplayName("투표 목록 조회 실패 - 비멤버")
    void getVotes_fail_notMember() {
        when(memberRepository.existsByUserIdAndTeamId(any(), any())).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> voteService.getVotes("30MNKA", "userId"));
    }

    @Test
    @DisplayName("투표 생성 성공 - 모임장")
    void createVote_success_leader() {
        VoteCreateRequest request = VoteCreateRequest.builder()
                .voteTitle("테스트 투표")
                .voteContent("투표 내용")
                .voteStartDt("2026-05-10")
                .voteEndDt("2026-05-20")
                .teamId("30MNKA")
                .options(List.of("선택지1", "선택지2"))
                .build();

        Member mockMember = mock(Member.class);
        when(mockMember.getMemRole()).thenReturn("L");
        when(memberRepository.findByUserIdAndTeamId(any(), any()))
                .thenReturn(Optional.of(mockMember));

        Vote mockVote = mock(Vote.class);
        when(mockVote.getVoteId()).thenReturn(1L);
        when(voteRepository.save(any())).thenReturn(mockVote);
        when(voteOptionRepository.saveAll(any())).thenReturn(List.of());

        assertDoesNotThrow(() -> voteService.createVote(request, "userId"));
    }

    @Test
    @DisplayName("투표 생성 실패 - 일반 멤버")
    void createVote_fail_notLeader() {
        VoteCreateRequest request = VoteCreateRequest.builder()
                .voteTitle("테스트 투표")
                .voteContent("투표 내용")
                .voteStartDt("2026-05-10")
                .voteEndDt("2026-05-20")
                .teamId("30MNKA")
                .options(List.of("선택지1", "선택지2"))
                .build();

        Member mockMember = mock(Member.class);
        when(mockMember.getMemRole()).thenReturn("M");
        when(memberRepository.findByUserIdAndTeamId(any(), any()))
                .thenReturn(Optional.of(mockMember));

        assertThrows(IllegalArgumentException.class,
                () -> voteService.createVote(request, "userId"));
    }

    @Test
    @DisplayName("투표하기 성공")
    void doVote_success() {
        VoteHistoryRequest request = VoteHistoryRequest.builder()
                .voteId(1L)
                .optSnList(List.of(1L))
                .build();

        Vote mockVote = mock(Vote.class);
        when(mockVote.getVoteMulti()).thenReturn("N");
        when(voteRepository.findById(any())).thenReturn(Optional.of(mockVote));
        when(voteHistoryRepository.existsByVoteIdAndMemId(any(), any())).thenReturn(false);
        when(voteHistoryRepository.saveAll(any())).thenReturn(List.of());

        assertDoesNotThrow(() -> voteService.doVote(request, "memId"));
    }

    @Test
    @DisplayName("투표하기 실패 - 중복 투표")
    void doVote_fail_duplicate() {
        VoteHistoryRequest request = VoteHistoryRequest.builder()
                .voteId(1L)
                .optSnList(List.of(1L))
                .build();

        Vote mockVote = mock(Vote.class);
        when(voteRepository.findById(any())).thenReturn(Optional.of(mockVote));
        when(voteHistoryRepository.existsByVoteIdAndMemId(any(), any())).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> voteService.doVote(request, "memId"));
    }

    @Test
    @DisplayName("투표 삭제 성공 - 모임장")
    void deleteVote_success_leader() {
        Vote mockVote = mock(Vote.class);
        when(mockVote.getTeamId()).thenReturn("30MNKA");
        when(voteRepository.findById(any())).thenReturn(Optional.of(mockVote));

        Member mockMember = mock(Member.class);
        when(mockMember.getMemRole()).thenReturn("L");
        when(memberRepository.findByUserIdAndTeamId(any(), any()))
                .thenReturn(Optional.of(mockMember));

        assertDoesNotThrow(() -> voteService.deleteVote(1L, "userId"));
        verify(voteRepository, times(1)).delete(any());
    }

    @Test
    @DisplayName("투표 삭제 실패 - 일반 멤버")
    void deleteVote_fail_notLeader() {
        Vote mockVote = mock(Vote.class);
        when(mockVote.getTeamId()).thenReturn("30MNKA");
        when(voteRepository.findById(any())).thenReturn(Optional.of(mockVote));

        Member mockMember = mock(Member.class);
        when(mockMember.getMemRole()).thenReturn("M");
        when(memberRepository.findByUserIdAndTeamId(any(), any()))
                .thenReturn(Optional.of(mockMember));

        assertThrows(IllegalArgumentException.class,
                () -> voteService.deleteVote(1L, "userId"));
        verify(voteRepository, never()).delete(any());
    }
}