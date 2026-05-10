package com.crewise.backend.domain.notice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class NoticeCreateRequest {

    @NotBlank
    private String notiTitle;

    private String notiContent;

    private String notiFix;

    @NotBlank
    private String teamId;
}