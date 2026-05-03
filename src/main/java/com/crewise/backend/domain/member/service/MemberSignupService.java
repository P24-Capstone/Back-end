package com.crewise.backend.domain.member.service;

import com.crewise.backend.domain.member.dto.MemberSignupRequest;
import com.crewise.backend.domain.member.dto.MemberSignupResponse;
import com.crewise.backend.domain.member.entity.MemberSignup;
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

    // 가입 신청 목록 조회 (모임장용)
    @Transactional(readOnly = true)
    public List<MemberSignupResponse> getSignups(String teamId) {
        return memberSignupRepository.findByTeamIdOrderByRegDtmDesc(teamId)
                .stream()
                .map(MemberSignupResponse::from)
                .collect(Collectors.toList());
    }

    // 가입 승인
    @Transactional
    public MemberSignupResponse approve(Long signupId) {
        MemberSignup signup = memberSignupRepository.findById(signupId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 신청입니다."));

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

    // 가입 거절
    @Transactional
    public MemberSignupResponse reject(Long signupId) {
        MemberSignup signup = memberSignupRepository.findById(signupId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 신청입니다."));

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