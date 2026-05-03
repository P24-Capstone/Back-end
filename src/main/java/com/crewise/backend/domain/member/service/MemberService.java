package com.crewise.backend.domain.member.service;

import com.crewise.backend.domain.member.dto.MemberCreateRequest;
import com.crewise.backend.domain.member.dto.MemberResponse;
import com.crewise.backend.domain.member.entity.Member;
import com.crewise.backend.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    // 모임원 목록 조회
    @Transactional(readOnly = true)
    public List<MemberResponse> getMembers(String teamId) {
        return memberRepository.findByTeamIdOrderByMemRoleAsc(teamId)
                .stream()
                .map(MemberResponse::from)
                .collect(Collectors.toList());
    }

    // 모임원 상세 조회
    @Transactional(readOnly = true)
    public MemberResponse getMember(String memId) {
        Member member = memberRepository.findById(memId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 모임원입니다."));
        return MemberResponse.from(member);
    }

    // 모임 가입 (모임원 생성)
    @Transactional
    public MemberResponse joinTeam(MemberCreateRequest request, String userId) {
        if (memberRepository.existsByUserIdAndTeamId(userId, request.getTeamId())) {
            throw new IllegalArgumentException("이미 가입된 모임입니다.");
        }

        Member member = Member.builder()
                .memId(generateMemId())
                .memNic(request.getMemNic())
                .memRole("M") // 기본 역할: 일반 멤버
                .memState("A") // 기본 상태: 활성
                .regDtm(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .userId(userId)
                .teamId(request.getTeamId())
                .build();

        return MemberResponse.from(memberRepository.save(member));
    }

    // 모임 탈퇴
    @Transactional
    public void leaveTeam(String memId, String userId) {
        Member member = memberRepository.findById(memId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 모임원입니다."));

        if (!member.getUserId().equals(userId)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        if ("L".equals(member.getMemRole())) {
            throw new IllegalArgumentException("모임장은 탈퇴할 수 없습니다. 모임장 권한을 위임한 후 탈퇴해주세요.");
        }

        memberRepository.delete(member);
    }

    private String generateMemId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 26);
    }
}