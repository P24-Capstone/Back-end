package com.crewise.backend.domain.vote.service;

import com.crewise.backend.domain.member.entity.Member;
import com.crewise.backend.domain.member.repository.MemberRepository;
import com.crewise.backend.domain.news.service.NewsService;
import com.crewise.backend.domain.vote.dto.VoteCreateRequest;
import com.crewise.backend.domain.vote.dto.VoteHistoryRequest;
import com.crewise.backend.domain.vote.dto.VoteResponse;
import com.crewise.backend.domain.vote.entity.Vote;
import com.crewise.backend.domain.vote.entity.VoteHistory;
import com.crewise.backend.domain.vote.entity.VoteOption;
import com.crewise.backend.domain.vote.repository.VoteHistoryRepository;
import com.crewise.backend.domain.vote.repository.VoteOptionRepository;
import com.crewise.backend.domain.vote.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final VoteOptionRepository voteOptionRepository;
    private final VoteHistoryRepository voteHistoryRepository;
    private final MemberRepository memberRepository;
    private final NewsService newsService;

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

    // 투표 목록 조회 (팀 멤버만)
    @Transactional(readOnly = true)
    public List<VoteResponse> getVotes(String teamId, String userId) {
        checkTeamMember(userId, teamId);
        return voteRepository.findByTeamIdOrderByVoteIdDesc(teamId)
                .stream()
                .map(vote -> {
                    List<VoteOption> options = voteOptionRepository.findByVoteId(vote.getVoteId());
                    List<Long> votedOptSns = voteHistoryRepository.findByVoteId(vote.getVoteId())
                            .stream()
                            .map(VoteHistory::getOptSn)
                            .collect(Collectors.toList());
                    return VoteResponse.from(vote, options, votedOptSns);
                })
                .collect(Collectors.toList());
    }

    // 투표 상세 조회 (팀 멤버만)
    @Transactional(readOnly = true)
    public VoteResponse getVote(Long voteId, String userId) {
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 투표입니다."));
        checkTeamMember(userId, vote.getTeamId());

        List<VoteOption> options = voteOptionRepository.findByVoteId(voteId);
        List<Long> votedOptSns = voteHistoryRepository.findByVoteId(voteId)
                .stream()
                .map(VoteHistory::getOptSn)
                .collect(Collectors.toList());

        return VoteResponse.from(vote, options, votedOptSns);
    }

    // 투표 생성 (모임장만)
    @Transactional
    public VoteResponse createVote(VoteCreateRequest request, String userId) {
        checkLeader(userId, request.getTeamId());

        Vote vote = Vote.builder()
                .voteTitle(request.getVoteTitle())
                .voteContent(request.getVoteContent())
                .voteStartDt(request.getVoteStartDt())
                .voteEndDt(request.getVoteEndDt())
                .voteType(request.getVoteType() != null ? request.getVoteType() : "N")
                .voteRule(request.getVoteRule() != null ? request.getVoteRule() : "V")
                .voteMulti(request.getVoteMulti() != null ? request.getVoteMulti() : "N")
                .voteRegDtm(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .teamId(request.getTeamId())
                .build();

        Vote savedVote = voteRepository.save(vote);

        // 선택지 저장
        List<VoteOption> options = request.getOptions().stream()
                .map(content -> VoteOption.builder()
                        .optContent(content)
                        .voteId(savedVote.getVoteId())
                        .build())
                .collect(Collectors.toList());
        voteOptionRepository.saveAll(options);

        newsService.createNews("V", savedVote.getVoteId(),
                "새 투표가 시작됐어요: " + savedVote.getVoteTitle(), savedVote.getTeamId());
        return VoteResponse.from(savedVote, options, List.of());
    }

    // 투표하기
    @Transactional
    public void doVote(VoteHistoryRequest request, String memId) {
        Vote vote = voteRepository.findById(request.getVoteId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 투표입니다."));

        // 이미 투표했는지 확인
        if (voteHistoryRepository.existsByVoteIdAndMemId(request.getVoteId(), memId)) {
            throw new IllegalArgumentException("이미 투표하셨습니다.");
        }

        // 다중 선택 불가인데 여러 개 선택한 경우
        if ("N".equals(vote.getVoteMulti()) && request.getOptSnList().size() > 1) {
            throw new IllegalArgumentException("단일 선택만 가능한 투표입니다.");
        }

        List<VoteHistory> histories = request.getOptSnList().stream()
                .map(optSn -> VoteHistory.builder()
                        .voteId(request.getVoteId())
                        .memId(memId)
                        .optSn(optSn)
                        .build())
                .collect(Collectors.toList());

        voteHistoryRepository.saveAll(histories);
    }

    // 투표 삭제 (모임장만)
    @Transactional
    public void deleteVote(Long voteId, String userId) {
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 투표입니다."));
        checkLeader(userId, vote.getTeamId());

        voteOptionRepository.deleteByVoteId(voteId);
        voteRepository.delete(vote);
    }
}