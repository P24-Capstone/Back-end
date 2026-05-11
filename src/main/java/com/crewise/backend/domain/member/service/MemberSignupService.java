package com.crewise.backend.domain.member.service;

import com.crewise.backend.domain.member.dto.MemberSignupRequest;
import com.crewise.backend.domain.member.dto.MemberSignupResponse;
import com.crewise.backend.domain.member.entity.Member;
import com.crewise.backend.domain.member.entity.MemberSignup;
import com.crewise.backend.domain.member.repository.MemberRepository;
import com.crewise.backend.domain.member.repository.MemberSignupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberSignupService {

    private final MemberSignupRepository memberSignupRepository;
    private final MemberRepository memberRepository;

    // 모임장 여부 확인
    private void checkLeader(String userId, String teamId) {
        Member member = memberRepository.findByUserIdAndTeamId(userId, teamId)
                .orElseThrow(() -> new IllegalArgumentException("해당 모임의 멤버가 아닙니다."));
        if (!"L".equals(member.getMemRole())) {
            throw new IllegalArgumentException("모임장만 가능한 작업입니다.");
        }
    }

    // 가입 신청
    @Transactional
    public MemberSignupResponse apply(MemberSignupRequest request, String userId) {
        if (memberSignupRepository.existsByUserIdAndTeamIdAndSignState(userId, request.getTeamId(), "W")) {
            throw new IllegalArgumentException("이미 가입 신청 중입니다.");
        }

        MemberSignup signup = MemberSignup.builder()
                .teamId(request.getTeamId())
                .userId(userId)
                .signState("W")
                .regDtm(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();

        return MemberSignupResponse.from(memberSignupRepository.save(signup));
    }

    // 가입 신청 목록 조회 (모임장만)
    @Transactional(readOnly = true)
    public List<MemberSignupResponse> getSignups(String teamId, String userId) {
        checkLeader(userId, teamId);
        return memberSignupRepository.findByTeamIdOrderByRegDtmDesc(teamId)
                .stream()
                .map(MemberSignupResponse::from)
                .collect(Collectors.toList());
    }

    // 가입 승인 (모임장만)
    @Transactional
    public MemberSignupResponse approve(Long signupId, String userId) {
        MemberSignup signup = memberSignupRepository.findById(signupId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 신청입니다."));
        checkLeader(userId, signup.getTeamId());

        MemberSignup updated = MemberSignup.builder()
                .signupId(signup.getSignupId())
                .teamId(signup.getTeamId())
                .userId(signup.getUserId())
                .signState("A")
                .regDtm(signup.getRegDtm())
                .procDtm(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();

        return MemberSignupResponse.from(memberSignupRepository.save(updated));
    }

    // 가입 거절 (모임장만)
    @Transactional
    public MemberSignupResponse reject(Long signupId, String userId) {
        MemberSignup signup = memberSignupRepository.findById(signupId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 신청입니다."));
        checkLeader(userId, signup.getTeamId());

        MemberSignup updated = MemberSignup.builder()
                .signupId(signup.getSignupId())
                .teamId(signup.getTeamId())
                .userId(signup.getUserId())
                .signState("R")
                .regDtm(signup.getRegDtm())
                .procDtm(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();

        return MemberSignupResponse.from(memberSignupRepository.save(updated));
    }

    // 가입 신청 취소
    @Transactional
    public void cancel(Long signupId, String userId) {
        MemberSignup signup = memberSignupRepository.findById(signupId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 신청입니다."));

        if (!signup.getUserId().equals(userId)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        memberSignupRepository.delete(signup);
    }
}