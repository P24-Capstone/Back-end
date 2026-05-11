package com.crewise.backend.domain.team.service;

import com.crewise.backend.domain.member.entity.Member;
import com.crewise.backend.domain.member.repository.MemberRepository;
import com.crewise.backend.domain.team.dto.TeamCreateRequest;
import com.crewise.backend.domain.team.entity.Team;
import com.crewise.backend.domain.team.repository.TeamRepository;
import com.crewise.backend.domain.user.entity.User;
import com.crewise.backend.domain.user.repository.UserRepository;
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
class TeamServiceTest {

        @InjectMocks
        private TeamService teamService;

        @Mock
        private TeamRepository teamRepository;

        @Mock
        private MemberRepository memberRepository;

        @Mock
        private UserRepository userRepository;

        @Test
        @DisplayName("내 모임 목록 조회 성공")
        void getMyTeams_success() {
                Member mockMember = mock(Member.class);
                when(mockMember.getTeamId()).thenReturn("30MNKA");
                when(memberRepository.findByUserIdAndMemState(any(), anyString())).thenReturn(List.of(mockMember));
                when(teamRepository.findAllById(any())).thenReturn(List.of());

                assertDoesNotThrow(() -> teamService.getMyTeams("userId"));
        }

        @Test
        @DisplayName("모임 생성 성공")
        void createTeam_success() {
                TeamCreateRequest request = TeamCreateRequest.builder()
                                .teamName("테스트 모임")
                                .teamInfo("모임 소개")
                                .teamCategory("독서")
                                .maxMembers(10)
                                .build();

                when(teamRepository.existsById(any())).thenReturn(false);
                when(teamRepository.save(any())).thenReturn(mock(Team.class));

                User mockUser = mock(User.class);
                when(mockUser.getUserName()).thenReturn("testUser");
                when(userRepository.findById(any())).thenReturn(Optional.of(mockUser));

                assertDoesNotThrow(() -> teamService.createTeam(request, "userId"));
                verify(teamRepository, times(1)).save(any());
        }

        @Test
        @DisplayName("모임 수정 성공 - 모임장")
        void updateTeam_success_leader() {
                TeamCreateRequest request = TeamCreateRequest.builder()
                                .teamName("수정된 모임")
                                .teamInfo("수정된 소개")
                                .teamCategory("운동")
                                .maxMembers(15)
                                .build();

                Team mockTeam = mock(Team.class);
                when(mockTeam.getTeamId()).thenReturn("30MNKA");
                when(mockTeam.getCurrentMember()).thenReturn(1);
                when(mockTeam.getCode()).thenReturn("ABCD1234");
                when(teamRepository.findById(any())).thenReturn(Optional.of(mockTeam));

                Member mockMember = mock(Member.class);
                when(mockMember.getMemRole()).thenReturn("L");
                when(memberRepository.findByUserIdAndTeamId(any(), any()))
                                .thenReturn(Optional.of(mockMember));
                when(teamRepository.save(any())).thenReturn(mock(Team.class));

                assertDoesNotThrow(() -> teamService.updateTeam("30MNKA", request, "userId"));
        }

        @Test
        @DisplayName("모임 수정 실패 - 일반 멤버")
        void updateTeam_fail_notLeader() {
                TeamCreateRequest request = TeamCreateRequest.builder()
                                .teamName("수정된 모임")
                                .teamInfo("수정된 소개")
                                .teamCategory("운동")
                                .maxMembers(15)
                                .build();

                Team mockTeam = mock(Team.class);
                when(teamRepository.findById(any())).thenReturn(Optional.of(mockTeam));

                Member mockMember = mock(Member.class);
                when(mockMember.getMemRole()).thenReturn("M");
                when(memberRepository.findByUserIdAndTeamId(any(), any()))
                                .thenReturn(Optional.of(mockMember));

                assertThrows(IllegalArgumentException.class,
                                () -> teamService.updateTeam("30MNKA", request, "userId"));
        }

        @Test
        @DisplayName("모임 삭제 성공 - 모임장")
        void deleteTeam_success_leader() {
                Team mockTeam = mock(Team.class);
                when(teamRepository.findById(any())).thenReturn(Optional.of(mockTeam));

                Member mockMember = mock(Member.class);
                when(mockMember.getMemRole()).thenReturn("L");
                when(memberRepository.findByUserIdAndTeamId(any(), any()))
                                .thenReturn(Optional.of(mockMember));

                assertDoesNotThrow(() -> teamService.deleteTeam("30MNKA", "userId"));
                verify(teamRepository, times(1)).delete(any());
        }

        @Test
        @DisplayName("모임 삭제 실패 - 일반 멤버")
        void deleteTeam_fail_notLeader() {
                Team mockTeam = mock(Team.class);
                when(teamRepository.findById(any())).thenReturn(Optional.of(mockTeam));

                Member mockMember = mock(Member.class);
                when(mockMember.getMemRole()).thenReturn("M");
                when(memberRepository.findByUserIdAndTeamId(any(), any()))
                                .thenReturn(Optional.of(mockMember));

                assertThrows(IllegalArgumentException.class,
                                () -> teamService.deleteTeam("30MNKA", "userId"));
                verify(teamRepository, never()).delete(any());
        }
}
