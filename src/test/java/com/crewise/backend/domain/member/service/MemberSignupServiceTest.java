package com.crewise.backend.domain.member.service;

import com.crewise.backend.domain.member.dto.MemberSignupRequest;
import com.crewise.backend.domain.member.entity.Member;
import com.crewise.backend.domain.member.entity.MemberSignup;
import com.crewise.backend.domain.member.repository.MemberRepository;
import com.crewise.backend.domain.member.repository.MemberSignupRepository;
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
class MemberSignupServiceTest {

    @InjectMocks
    private MemberSignupService memberSignupService;

    @Mock
    private MemberSignupRepository memberSignupRepository;

    @Mock
    private MemberRepository memberRepository;

    @Test
    @DisplayName("가입 신청 성공")
    void apply_success() {
        MemberSignupRequest request = MemberSignupRequest.builder()
                .teamId("30MNKA")
                .build();

        when(memberSignupRepository.existsByUserIdAndTeamIdAndSignState(any(), any(), any()))
                .thenReturn(false);
        when(memberSignupRepository.save(any())).thenReturn(mock(MemberSignup.class));

        assertDoesNotThrow(() -> memberSignupService.apply(request, "userId"));
    }

    @Test
    @DisplayName("가입 신청 실패 - 중복 신청")
    void apply_fail_duplicate() {
        MemberSignupRequest request = MemberSignupRequest.builder()
                .teamId("30MNKA")
                .build();

        when(memberSignupRepository.existsByUserIdAndTeamIdAndSignState(any(), any(), any()))
                .thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> memberSignupService.apply(request, "userId"));
    }

    @Test
    @DisplayName("가입 신청 목록 조회 성공 - 모임장")
    void getSignups_success_leader() {
        Member mockMember = mock(Member.class);
        when(mockMember.getMemRole()).thenReturn("L");
        when(memberRepository.findByUserIdAndTeamId(any(), any()))
                .thenReturn(Optional.of(mockMember));
        when(memberSignupRepository.findByTeamIdOrderByRegDtmDesc(any()))
                .thenReturn(List.of());

        assertDoesNotThrow(() -> memberSignupService.getSignups("30MNKA", "userId"));
    }

    @Test
    @DisplayName("가입 신청 목록 조회 실패 - 일반 멤버")
    void getSignups_fail_notLeader() {
        Member mockMember = mock(Member.class);
        when(mockMember.getMemRole()).thenReturn("M");
        when(memberRepository.findByUserIdAndTeamId(any(), any()))
                .thenReturn(Optional.of(mockMember));

        assertThrows(IllegalArgumentException.class,
                () -> memberSignupService.getSignups("30MNKA", "userId"));
    }

    @Test
    @DisplayName("가입 승인 성공 - 모임장")
    void approve_success_leader() {
        MemberSignup mockSignup = mock(MemberSignup.class);
        when(mockSignup.getTeamId()).thenReturn("30MNKA");
        when(memberSignupRepository.findById(any())).thenReturn(Optional.of(mockSignup));

        Member mockMember = mock(Member.class);
        when(mockMember.getMemRole()).thenReturn("L");
        when(memberRepository.findByUserIdAndTeamId(any(), any()))
                .thenReturn(Optional.of(mockMember));
        when(memberSignupRepository.save(any())).thenReturn(mock(MemberSignup.class));

        assertDoesNotThrow(() -> memberSignupService.approve(1L, "userId"));
    }

    @Test
    @DisplayName("가입 승인 실패 - 일반 멤버")
    void approve_fail_notLeader() {
        MemberSignup mockSignup = mock(MemberSignup.class);
        when(mockSignup.getTeamId()).thenReturn("30MNKA");
        when(memberSignupRepository.findById(any())).thenReturn(Optional.of(mockSignup));

        Member mockMember = mock(Member.class);
        when(mockMember.getMemRole()).thenReturn("M");
        when(memberRepository.findByUserIdAndTeamId(any(), any()))
                .thenReturn(Optional.of(mockMember));

        assertThrows(IllegalArgumentException.class,
                () -> memberSignupService.approve(1L, "userId"));
    }

    @Test
    @DisplayName("가입 신청 취소 성공")
    void cancel_success() {
        MemberSignup mockSignup = mock(MemberSignup.class);
        when(mockSignup.getUserId()).thenReturn("userId");
        when(memberSignupRepository.findById(any())).thenReturn(Optional.of(mockSignup));

        assertDoesNotThrow(() -> memberSignupService.cancel(1L, "userId"));
        verify(memberSignupRepository, times(1)).delete(any());
    }

    @Test
    @DisplayName("가입 신청 취소 실패 - 다른 사용자")
    void cancel_fail_notOwner() {
        MemberSignup mockSignup = mock(MemberSignup.class);
        when(mockSignup.getUserId()).thenReturn("otherUserId");
        when(memberSignupRepository.findById(any())).thenReturn(Optional.of(mockSignup));

        assertThrows(IllegalArgumentException.class,
                () -> memberSignupService.cancel(1L, "userId"));
        verify(memberSignupRepository, never()).delete(any());
    }
}