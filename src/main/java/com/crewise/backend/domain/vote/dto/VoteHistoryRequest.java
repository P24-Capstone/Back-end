package com.crewise.backend.domain.vote.dto;

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
public class VoteHistoryRequest {

    @NotNull
    private Long voteId;

    @NotNull
    private List<Long> optSnList;
}