package com.crewise.backend.domain.team.service;

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

    // 모임 목록 조회
    @Transactional(readOnly = true)
    public List<TeamResponse> getTeams(String teamName) {
        List<Team> teams;
        if (teamName != null && !teamName.isEmpty()) {
            teams = teamRepository.findByTeamNameContaining(teamName);
        } else {
            teams = teamRepository.findAllByOrderByTeamIdDesc();
        }
        return teams.stream()
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
    public TeamResponse createTeam(TeamCreateRequest request) {
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