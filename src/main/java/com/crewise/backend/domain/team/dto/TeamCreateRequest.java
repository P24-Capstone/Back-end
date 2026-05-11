package com.crewise.backend.domain.team.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamCreateRequest {

    @NotBlank
    private String teamId;

    @NotBlank
    private String teamName;

    private String teamImg;

    private String teamInfo;

    private String teamCategory;

    @NotNull
    @Max(15)
    private Integer maxMembers;
}