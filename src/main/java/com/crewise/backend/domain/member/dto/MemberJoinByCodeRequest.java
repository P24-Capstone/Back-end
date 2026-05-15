package com.crewise.backend.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberJoinByCodeRequest {

    @NotBlank
    private String memNic;

    @NotBlank
    private String code;

    private Long userImgId;
}