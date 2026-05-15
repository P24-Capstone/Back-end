package com.crewise.backend.domain.mission.service;

import com.crewise.backend.domain.member.entity.Member;
import com.crewise.backend.domain.member.repository.MemberRepository;
import com.crewise.backend.domain.mission.dto.MissionCreateRequest;
import com.crewise.backend.domain.mission.dto.MissionResponse;
import com.crewise.backend.domain.mission.dto.MissionVerifyRequest;
import com.crewise.backend.domain.mission.entity.Mission;
import com.crewise.backend.domain.mission.entity.MissionVerify;
import com.crewise.backend.domain.mission.repository.MissionRepository;
import com.crewise.backend.domain.mission.repository.MissionVerifyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MissionService {

    private final MissionRepository missionRepository;
    private final MissionVerifyRepository missionVerifyRepository;
    private final MemberRepository memberRepository;
    private final RestTemplate restTemplate;

    @Value("${ai.serving.url}")
    private String aiServingUrl;

    // 팀 멤버 확인
    private void checkTeamMember(String userId, String teamId) {
        if (!memberRepository.existsByUserIdAndTeamId(userId, teamId)) {
            throw new IllegalArgumentException("해당 모임의 멤버가 아닙니다.");
        }
    }

    // 모임장 확인
    private void checkLeader(String userId, String teamId) {
        Member member = memberRepository.findByUserIdAndTeamId(userId, teamId)
                .orElseThrow(() -> new IllegalArgumentException("해당 모임의 멤버가 아닙니다."));
        if (!"L".equals(member.getMemRole())) {
            throw new IllegalArgumentException("모임장만 가능한 작업입니다.");
        }
    }

    // 미션 목록 조회 (팀 멤버만)
    @Transactional(readOnly = true)
    public List<MissionResponse> getMissions(String teamId, String userId) {
        checkTeamMember(userId, teamId);
        return missionRepository.findByTeamIdOrderByMissionIdDesc(teamId)
                .stream()
                .map(MissionResponse::from)
                .collect(Collectors.toList());
    }

    // 미션 상세 조회 (팀 멤버만)
    @Transactional(readOnly = true)
    public MissionResponse getMission(Long missionId, String userId) {
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 미션입니다."));
        checkTeamMember(userId, mission.getTeamId());
        return MissionResponse.from(mission);
    }

    // 미션 생성 (모임장만)
    @Transactional
    public MissionResponse createMission(MissionCreateRequest request, String userId) {
        checkLeader(userId, request.getTeamId());

        Mission mission = Mission.builder()
                .missionTitle(request.getMissionTitle())
                .missionContent(request.getMissionContent())
                .missionType(request.getMissionType())
                .verifyPrompt(request.getVerifyPrompt())
                .missionStartDtm(request.getMissionStartDtm())
                .missionEndDtm(request.getMissionEndDtm())
                .teamId(request.getTeamId())
                .build();

        return MissionResponse.from(missionRepository.save(mission));
    }

    // 미션 삭제 (모임장만)
    @Transactional
    public void deleteMission(Long missionId, String userId) {
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 미션입니다."));
        checkLeader(userId, mission.getTeamId());
        missionRepository.delete(mission);
    }

    // 미션 인증 (FastAPI 호출)
    @Transactional
    public MissionVerify verifyMission(MissionVerifyRequest request, String memId) {
        Mission mission = missionRepository.findById(request.getMissionId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 미션입니다."));

        Map<String, Object> body = new HashMap<>();
        body.put("mission_content", mission.getMissionContent());
        body.put("verify_prompt", mission.getVerifyPrompt());
        body.put("verify_content", request.getVerifyContent());
        body.put("image_url", request.getImageUrl());

        Map<String, Object> aiResponse = restTemplate.postForObject(
                aiServingUrl + "/verify",
                body,
                Map.class);

        String aiRejectYn = Boolean.TRUE.equals(aiResponse.get("rejected")) ? "Y" : "N";
        String aiResult = (String) aiResponse.get("reason");

        MissionVerify verify = MissionVerify.builder()
                .verifyContent(request.getVerifyContent())
                .verifyRegDtm(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .aiRejectYn(aiRejectYn)
                .aiResult(aiResult)
                .verifyState("P")
                .missionId(request.getMissionId())
                .memId(memId)
                .build();

        return missionVerifyRepository.save(verify);
    }
}