package com.crewise.backend.domain.member.dto;

import lombok.Getter;

@Getter
public class MemberUpdateRequest {
    private String memNic;
    private Long userImgId;
}
