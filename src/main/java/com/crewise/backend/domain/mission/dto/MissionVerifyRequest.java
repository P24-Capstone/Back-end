package com.crewise.backend.domain.mission.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MissionVerifyRequest {

    @NotNull
    private Long missionId;

    private String verifyContent;

    private String imageUrl;
}