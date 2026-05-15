package com.crewise.backend.domain.mission.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MissionCreateRequest {

    @NotBlank
    private String missionTitle;

    @NotBlank
    private String missionContent;

    @NotBlank
    private String missionType;

    private String verifyPrompt;

    @NotBlank
    private String missionStartDtm;

    @NotBlank
    private String missionEndDtm;

    @NotBlank
    private String teamId;
}