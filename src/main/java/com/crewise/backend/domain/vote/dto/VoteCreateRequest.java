package com.crewise.backend.domain.vote.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoteCreateRequest {

    @NotBlank
    private String voteTitle;

    @NotBlank
    private String voteContent;

    @NotBlank
    private String voteStartDt;

    @NotBlank
    private String voteEndDt;

    private String voteType;
    private String voteRule;
    private String voteMulti;

    @NotBlank
    private String teamId;

    @NotNull
    private List<String> options;
}