package com.crewise.backend.domain.mission.service;

import com.crewise.backend.domain.member.entity.Member;
import com.crewise.backend.domain.member.repository.MemberRepository;
import com.crewise.backend.domain.mission.dto.MissionCreateRequest;
import com.crewise.backend.domain.mission.entity.Mission;
import com.crewise.backend.domain.mission.repository.MissionRepository;
import com.crewise.backend.domain.mission.repository.MissionVerifyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MissionServiceTest {

    @InjectMocks
    private MissionService missionService;

    @Mock
    private MissionRepository missionRepository;

    @Mock
    private MissionVerifyRepository missionVerifyRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private RestTemplate restTemplate;

    @Test
    @DisplayName("미션 목록 조회 성공 - 팀 멤버")
    void getMissions_success() {
        when(memberRepository.existsByUserIdAndTeamId(any(), any())).thenReturn(true);
        when(missionRepository.findByTeamIdOrderByMissionIdDesc(any())).thenReturn(List.of());

        assertDoesNotThrow(() -> missionService.getMissions("30MNKA", "userId"));
    }

    @Test
    @DisplayName("미션 목록 조회 실패 - 비멤버")
    void getMissions_fail_notMember() {
        when(memberRepository.existsByUserIdAndTeamId(any(), any())).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> missionService.getMissions("30MNKA", "userId"));
    }

    @Test
    @DisplayName("미션 생성 성공 - 모임장")
    void createMission_success_leader() {
        MissionCreateRequest request = MissionCreateRequest.builder()
                .missionTitle("테스트 미션")
                .missionContent("미션 내용")
                .missionType("C")
                .missionStartDtm("2026-05-10 00:00:00")
                .missionEndDtm("2026-05-20 00:00:00")
                .teamId("30MNKA")
                .build();

        Member mockMember = mock(Member.class);
        when(mockMember.getMemRole()).thenReturn("L");
        when(memberRepository.findByUserIdAndTeamId(any(), any()))
                .thenReturn(Optional.of(mockMember));
        when(missionRepository.save(any())).thenReturn(mock(Mission.class));

        assertDoesNotThrow(() -> missionService.createMission(request, "userId"));
    }

    @Test
    @DisplayName("미션 생성 실패 - 일반 멤버")
    void createMission_fail_notLeader() {
        MissionCreateRequest request = MissionCreateRequest.builder()
                .missionTitle("테스트 미션")
                .missionContent("미션 내용")
                .missionType("C")
                .missionStartDtm("2026-05-10 00:00:00")
                .missionEndDtm("2026-05-20 00:00:00")
                .teamId("30MNKA")
                .build();

        Member mockMember = mock(Member.class);
        when(mockMember.getMemRole()).thenReturn("M");
        when(memberRepository.findByUserIdAndTeamId(any(), any()))
                .thenReturn(Optional.of(mockMember));

        assertThrows(IllegalArgumentException.class,
                () -> missionService.createMission(request, "userId"));
    }

    @Test
    @DisplayName("미션 삭제 성공 - 모임장")
    void deleteMission_success_leader() {
        Mission mockMission = mock(Mission.class);
        when(mockMission.getTeamId()).thenReturn("30MNKA");
        when(missionRepository.findById(any())).thenReturn(Optional.of(mockMission));

        Member mockMember = mock(Member.class);
        when(mockMember.getMemRole()).thenReturn("L");
        when(memberRepository.findByUserIdAndTeamId(any(), any()))
                .thenReturn(Optional.of(mockMember));

        assertDoesNotThrow(() -> missionService.deleteMission(1L, "userId"));
        verify(missionRepository, times(1)).delete(any());
    }

    @Test
    @DisplayName("미션 삭제 실패 - 일반 멤버")
    void deleteMission_fail_notLeader() {
        Mission mockMission = mock(Mission.class);
        when(mockMission.getTeamId()).thenReturn("30MNKA");
        when(missionRepository.findById(any())).thenReturn(Optional.of(mockMission));

        Member mockMember = mock(Member.class);
        when(mockMember.getMemRole()).thenReturn("M");
        when(memberRepository.findByUserIdAndTeamId(any(), any()))
                .thenReturn(Optional.of(mockMember));

        assertThrows(IllegalArgumentException.class,
                () -> missionService.deleteMission(1L, "userId"));
        verify(missionRepository, never()).delete(any());
    }
}