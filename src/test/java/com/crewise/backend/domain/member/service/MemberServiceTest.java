package com.crewise.backend.domain.member.service;

import com.crewise.backend.domain.member.dto.MemberCreateRequest;
import com.crewise.backend.domain.member.dto.MemberJoinByCodeRequest;
import com.crewise.backend.domain.member.entity.Member;
import com.crewise.backend.domain.member.repository.MemberRepository;
import com.crewise.backend.domain.team.entity.Team;
import com.crewise.backend.domain.team.repository.TeamRepository;
import com.crewise.backend.domain.user.repository.UserImgRepository;
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
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private UserImgRepository userImgRepository;

    @Test
    @DisplayName("모임 가입 성공")
    void joinTeam_success() {
        MemberCreateRequest request = MemberCreateRequest.builder()
                .memNic("테스트닉네임")
                .teamId("30MNKA")
                .build();

        when(memberRepository.existsByUserIdAndTeamId(any(), any())).thenReturn(false);
        when(memberRepository.save(any())).thenReturn(mock(Member.class));

        assertDoesNotThrow(() -> memberService.joinTeam(request, "userId"));
        verify(memberRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("모임 가입 실패 - 중복 가입")
    void joinTeam_fail_duplicate() {
        MemberCreateRequest request = MemberCreateRequest.builder()
                .memNic("테스트닉네임")
                .teamId("30MNKA")
                .build();

        when(memberRepository.existsByUserIdAndTeamId(any(), any())).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> memberService.joinTeam(request, "userId"));
    }

    @Test
    @DisplayName("추천코드로 모임 가입 성공")
    void joinTeamByCode_success() {
        MemberJoinByCodeRequest request = MemberJoinByCodeRequest.builder()
                .memNic("테스트닉네임")
                .code("ABCD1234")
                .build();

        Team mockTeam = mock(Team.class);
        when(mockTeam.getTeamId()).thenReturn("30MNKA");
        when(teamRepository.findByCode(any())).thenReturn(Optional.of(mockTeam));
        when(memberRepository.existsByUserIdAndTeamId(any(), any())).thenReturn(false);
        when(memberRepository.save(any())).thenReturn(mock(Member.class));

        assertDoesNotThrow(() -> memberService.joinTeamByCode(request, "userId"));
    }

    @Test
    @DisplayName("추천코드로 모임 가입 실패 - 유효하지 않은 코드")
    void joinTeamByCode_fail_invalidCode() {
        MemberJoinByCodeRequest request = MemberJoinByCodeRequest.builder()
                .memNic("테스트닉네임")
                .code("INVALID")
                .build();

        when(teamRepository.findByCode(any())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> memberService.joinTeamByCode(request, "userId"));
    }

    @Test
    @DisplayName("가입 승인 성공 - 모임장")
    void approveMember_success() {
        Member mockMember = mock(Member.class);
        when(mockMember.getTeamId()).thenReturn("30MNKA");
        when(mockMember.getMemState()).thenReturn("W");
        when(memberRepository.findById(any())).thenReturn(Optional.of(mockMember));

        Member leaderMock = mock(Member.class);
        when(leaderMock.getMemRole()).thenReturn("L");
        when(leaderMock.getMemState()).thenReturn("A");
        when(memberRepository.findByUserIdAndTeamId(any(), any()))
                .thenReturn(Optional.of(leaderMock));

        assertDoesNotThrow(() -> memberService.approveMember("memId", "userId"));
        verify(mockMember, times(1)).approve(any());
    }

    @Test
    @DisplayName("가입 승인 실패 - 일반 멤버")
    void approveMember_fail_notLeader() {
        Member mockMember = mock(Member.class);
        when(mockMember.getTeamId()).thenReturn("30MNKA");
        when(memberRepository.findById(any())).thenReturn(Optional.of(mockMember));

        Member normalMock = mock(Member.class);
        when(normalMock.getMemRole()).thenReturn("M");
        when(memberRepository.findByUserIdAndTeamId(any(), any()))
                .thenReturn(Optional.of(normalMock));

        assertThrows(IllegalArgumentException.class,
                () -> memberService.approveMember("memId", "userId"));
    }

    @Test
    @DisplayName("모임 탈퇴 성공")
    void leaveTeam_success() {
        Member mockMember = mock(Member.class);
        when(mockMember.getUserId()).thenReturn("userId");
        when(mockMember.getMemRole()).thenReturn("M");
        when(memberRepository.findById(any())).thenReturn(Optional.of(mockMember));

        assertDoesNotThrow(() -> memberService.leaveTeam("memId", "userId"));
        verify(memberRepository, times(1)).delete(any());
    }

    @Test
    @DisplayName("모임 탈퇴 실패 - 모임장")
    void leaveTeam_fail_leader() {
        Member mockMember = mock(Member.class);
        when(mockMember.getUserId()).thenReturn("userId");
        when(mockMember.getMemRole()).thenReturn("L");
        when(memberRepository.findById(any())).thenReturn(Optional.of(mockMember));

        assertThrows(IllegalArgumentException.class,
                () -> memberService.leaveTeam("memId", "userId"));
        verify(memberRepository, never()).delete(any());
    }
}