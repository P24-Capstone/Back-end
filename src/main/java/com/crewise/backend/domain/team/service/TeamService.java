package com.crewise.backend.domain.team.service;

import com.crewise.backend.domain.member.entity.Member;
import com.crewise.backend.domain.member.repository.MemberRepository;
import com.crewise.backend.domain.team.dto.TeamCreateRequest;
import com.crewise.backend.domain.team.dto.TeamResponse;
import com.crewise.backend.domain.team.entity.Team;
import com.crewise.backend.domain.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final MemberRepository memberRepository;

    // 모임장 여부 확인
    private void checkLeader(String userId, String teamId) {
        Member member = memberRepository.findByUserIdAndTeamId(userId, teamId)
                .orElseThrow(() -> new IllegalArgumentException("해당 모임의 멤버가 아닙니다."));
        if (!"L".equals(member.getMemRole())) {
            throw new IllegalArgumentException("모임장만 가능한 작업입니다.");
        }
    }

    // 내가 가입한 모임 목록 조회
    @Transactional(readOnly = true)
    public List<TeamResponse> getMyTeams(String userId) {
        List<String> teamIds = memberRepository.findByUserId(userId)
                .stream()
                .map(Member::getTeamId)
                .collect(Collectors.toList());

        return teamRepository.findAllById(teamIds)
                .stream()
                .map(TeamResponse::from)
                .collect(Collectors.toList());
    }

    // 모임 상세 조회
    @Transactional(readOnly = true)
    public TeamResponse getTeam(String teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 모임입니다."));
        return TeamResponse.from(team);
    }

    // 모임 생성
    @Transactional
    public TeamResponse createTeam(TeamCreateRequest request, String userId) {
        String teamId = generateTeamId();
        String code = generateCode();

        Team team = Team.builder()
                .teamId(teamId)
                .teamName(request.getTeamName())
                .teamImg(request.getTeamImg())
                .teamInfo(request.getTeamInfo())
                .teamCategory(request.getTeamCategory())
                .maxMembers(request.getMaxMembers())
                .currentMember(1)
                .code(code)
                .build();

        teamRepository.save(team);
        return TeamResponse.from(team);
    }

    // 모임 수정 (모임장만)
    @Transactional
    public TeamResponse updateTeam(String teamId, TeamCreateRequest request, String userId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 모임입니다."));
        checkLeader(userId, teamId);

        Team updated = Team.builder()
                .teamId(team.getTeamId())
                .teamName(request.getTeamName())
                .teamImg(request.getTeamImg())
                .teamInfo(request.getTeamInfo())
                .teamCategory(request.getTeamCategory())
                .maxMembers(request.getMaxMembers())
                .currentMember(team.getCurrentMember())
                .code(team.getCode())
                .build();

        return TeamResponse.from(teamRepository.save(updated));
    }

    // 모임 삭제 (모임장만)
    @Transactional
    public void deleteTeam(String teamId, String userId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 모임입니다."));
        checkLeader(userId, teamId);
        teamRepository.delete(team);
    }

    // 6자리 랜덤 모임 ID 생성
    private String generateTeamId() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        String teamId;
        do {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 6; i++) {
                sb.append(chars.charAt(random.nextInt(chars.length())));
            }
            teamId = sb.toString();
        } while (teamRepository.existsById(teamId));
        return teamId;
    }

    // 초대코드 생성
    private String generateCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}