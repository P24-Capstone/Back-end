package com.crewise.backend.domain.team.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class TeamCreateRequest {

    @NotBlank
    private String teamName;

    private String teamImg;

    private String teamInfo;

    private String teamCategory;

    @NotNull
    @Max(15)
    private Integer maxMembers;
}