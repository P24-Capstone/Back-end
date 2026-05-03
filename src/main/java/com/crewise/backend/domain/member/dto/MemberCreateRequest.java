package com.crewise.backend.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class MemberCreateRequest {

    @NotBlank
    private String memNic;

    @NotBlank
    private String teamId;
}