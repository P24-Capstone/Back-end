package com.crewise.backend.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class MemberJoinByCodeRequest {

    @NotBlank
    private String memNic;

    @NotBlank
    private String code;
}
