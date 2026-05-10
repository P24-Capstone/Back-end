package com.crewise.backend.domain.notice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeCreateRequest {

    @NotBlank
    private String notiTitle;

    private String notiContent;

    private String notiFix;

    @NotBlank
    private String teamId;
}