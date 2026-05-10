package com.crewise.backend.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class MemberSignupRequest {

    @NotBlank
    private String teamId;
}