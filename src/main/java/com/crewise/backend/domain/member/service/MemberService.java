package com.crewise.backend.domain.member.service;

import com.crewise.backend.domain.member.dto.MemberCreateRequest;
import com.crewise.backend.domain.member.dto.MemberJoinByCodeRequest;
import com.crewise.backend.domain.member.dto.MemberResponse;
import com.crewise.backend.domain.member.dto.MemberUpdateRequest;
import com.crewise.backend.domain.member.entity.Member;
import com.crewise.backend.domain.member.repository.MemberRepository;
import com.crewise.backend.domain.news.service.NewsService;
import com.crewise.backend.domain.team.entity.Team;
import com.crewise.backend.domain.team.repository.TeamRepository;
import com.crewise.backend.domain.user.entity.UserImg;
import com.crewise.backend.domain.user.repository.UserImgRepository;
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
    private final TeamRepository teamRepository;
    private final UserImgRepository userImgRepository;
    private final NewsService newsService;

    // 이미지 URL 조회
    private String getImgFileKey(Long userImgId) {
        if (userImgId == null)
            return null;
        return userImgRepository.findById(userImgId)
                .map(UserImg::getImgFileKey)
                .orElse(null);
    }

    // 모임원 목록 조회 (리더: 전체 상태, 일반: A 상태만)
    @Transactional(readOnly = true)
    public List<MemberResponse> getMembers(String teamId, String userId) {
        boolean isLeader = userId != null && memberRepository.findByUserIdAndTeamId(userId, teamId)
                .map(m -> "L".equals(m.getMemRole()) && "A".equals(m.getMemState()))
                .orElse(false);

        if (isLeader) {
            return memberRepository.findByTeamIdOrderByMemRoleAsc(teamId)
                    .stream()
                    .map(m -> MemberResponse.from(m, getImgFileKey(m.getUserImgId())))
                    .collect(Collectors.toList());
        }
        return memberRepository.findByTeamIdAndMemStateOrderByMemRoleAsc(teamId, "A")
                .stream()
                .map(m -> MemberResponse.from(m, getImgFileKey(m.getUserImgId())))
                .collect(Collectors.toList());
    }

    // 내 멤버 정보 조회
    @Transactional(readOnly = true)
    public MemberResponse getMyMembership(String teamId, String userId) {
        Member member = memberRepository.findByUserIdAndTeamId(userId, teamId)
                .orElseThrow(() -> new IllegalArgumentException("해당 모임의 멤버가 아닙니다."));
        return MemberResponse.from(member, getImgFileKey(member.getUserImgId()));
    }

    // 모임원 상세 조회
    @Transactional(readOnly = true)
    public MemberResponse getMember(String memId) {
        Member member = memberRepository.findById(memId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 모임원입니다."));
        return MemberResponse.from(member, getImgFileKey(member.getUserImgId()));
    }

    // 모임 가입 (모임원 생성)
    @Transactional
    public MemberResponse joinTeam(MemberCreateRequest request, String userId) {
        if (memberRepository.existsByUserIdAndTeamId(userId, request.getTeamId())) {
            throw new IllegalArgumentException("이미 가입된 모임입니다.");
        }

        if (request.getUserImgId() != null) {
            userImgRepository.findById(request.getUserImgId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이미지입니다."));
        }

        Member member = Member.builder()
                .memId(generateMemId())
                .memNic(request.getMemNic())
                .memRole("M")
                .memState("W")
                .regDtm(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .userId(userId)
                .teamId(request.getTeamId())
                .userImgId(request.getUserImgId())
                .build();

        Member saved = memberRepository.save(member);
        return MemberResponse.from(saved, getImgFileKey(saved.getUserImgId()));
    }

    // 추천코드로 모임 가입
    @Transactional
    public MemberResponse joinTeamByCode(MemberJoinByCodeRequest request, String userId) {
        Team team = teamRepository.findByCode(request.getCode())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 추천코드입니다."));

        if (memberRepository.existsByUserIdAndTeamId(userId, team.getTeamId())) {
            throw new IllegalArgumentException("이미 가입된 모임입니다.");
        }

        if (request.getUserImgId() != null) {
            userImgRepository.findById(request.getUserImgId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이미지입니다."));
        }

        Member member = Member.builder()
                .memId(generateMemId())
                .memNic(request.getMemNic())
                .memRole("M")
                .memState("W")
                .regDtm(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .userId(userId)
                .teamId(team.getTeamId())
                .userImgId(request.getUserImgId())
                .build();

        Member saved = memberRepository.save(member);
        return MemberResponse.from(saved, getImgFileKey(saved.getUserImgId()));
    }

    // 모임원 정보 수정 (본인만)
    @Transactional
    public MemberResponse updateMember(String memId, MemberUpdateRequest request, String userId) {
        Member member = memberRepository.findById(memId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 모임원입니다."));

        if (!member.getUserId().equals(userId)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        if (request.getUserImgId() != null) {
            userImgRepository.findById(request.getUserImgId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이미지입니다."));
        }

        member.update(request.getMemNic(), request.getUserImgId());
        return MemberResponse.from(member, getImgFileKey(member.getUserImgId()));
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

    // 가입 대기 목록 조회 (모임장만)
    @Transactional(readOnly = true)
    public List<MemberResponse> getPendingMembers(String teamId, String userId) {
        checkLeader(userId, teamId);
        return memberRepository.findByTeamIdAndMemStateOrderByRegDtmDesc(teamId, "W")
                .stream()
                .map(m -> MemberResponse.from(m, getImgFileKey(m.getUserImgId())))
                .collect(Collectors.toList());
    }

    // 가입 승인 (모임장만)
    @Transactional
    public MemberResponse approveMember(String memId, String userId) {
        Member member = memberRepository.findById(memId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 가입 신청입니다."));
        checkLeader(userId, member.getTeamId());

        if (!"W".equals(member.getMemState())) {
            throw new IllegalArgumentException("대기 상태인 가입 신청만 승인할 수 있습니다.");
        }

        member.approve(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        newsService.createNews("M", null,
                member.getMemNic() + "님이 모임에 가입했어요!", member.getTeamId());
        return MemberResponse.from(member, getImgFileKey(member.getUserImgId()));
    }

    // 가입 거절 (모임장만)
    @Transactional
    public MemberResponse rejectMember(String memId, String userId) {
        Member member = memberRepository.findById(memId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 가입 신청입니다."));
        checkLeader(userId, member.getTeamId());

        if (!"W".equals(member.getMemState())) {
            throw new IllegalArgumentException("대기 상태인 가입 신청만 거절할 수 있습니다.");
        }

        member.reject(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return MemberResponse.from(member, getImgFileKey(member.getUserImgId()));
    }

    // 모임장 권한 검증
    private void checkLeader(String userId, String teamId) {
        Member member = memberRepository.findByUserIdAndTeamId(userId, teamId)
                .orElseThrow(() -> new IllegalArgumentException("해당 모임의 멤버가 아닙니다."));
        if (!"L".equals(member.getMemRole()) || !"A".equals(member.getMemState())) {
            throw new IllegalArgumentException("모임장만 가능한 작업입니다.");
        }
    }

    private String generateMemId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 26);
    }
}