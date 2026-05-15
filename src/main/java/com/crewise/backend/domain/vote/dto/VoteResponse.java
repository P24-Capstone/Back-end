package com.crewise.backend.domain.vote.dto;

import com.crewise.backend.domain.vote.entity.Vote;
import com.crewise.backend.domain.vote.entity.VoteOption;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class VoteResponse {

    private Long voteId;
    private String voteTitle;
    private String voteContent;
    private String voteStartDt;
    private String voteEndDt;
    private String voteType;
    private String voteRule;
    private String voteMulti;
    private String voteRegDtm;
    private String teamId;
    private List<VoteOptionDto> options;

    @Getter
    public static class VoteOptionDto {
        private Long optSn;
        private String optContent;
        private int voteCount;

        public static VoteOptionDto from(VoteOption option, int count) {
            VoteOptionDto dto = new VoteOptionDto();
            dto.optSn = option.getOptSn();
            dto.optContent = option.getOptContent();
            dto.voteCount = count;
            return dto;
        }
    }

    public static VoteResponse from(Vote vote, List<VoteOption> options, List<Long> votedOptSns) {
        VoteResponse response = new VoteResponse();
        response.voteId = vote.getVoteId();
        response.voteTitle = vote.getVoteTitle();
        response.voteContent = vote.getVoteContent();
        response.voteStartDt = vote.getVoteStartDt();
        response.voteEndDt = vote.getVoteEndDt();
        response.voteType = vote.getVoteType();
        response.voteRule = vote.getVoteRule();
        response.voteMulti = vote.getVoteMulti();
        response.voteRegDtm = vote.getVoteRegDtm();
        response.teamId = vote.getTeamId();
        response.options = options.stream()
                .map(opt -> VoteOptionDto.from(opt,
                        (int) votedOptSns.stream().filter(id -> id.equals(opt.getOptSn())).count()))
                .collect(Collectors.toList());
        return response;
    }
}