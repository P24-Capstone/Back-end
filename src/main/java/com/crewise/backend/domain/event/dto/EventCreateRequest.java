package com.crewise.backend.domain.event.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventCreateRequest {

    @NotBlank
    private String evtTitle;

    @NotBlank
    private String evtContent;

    @NotBlank
    private String evtStartDt;

    @NotBlank
    private String evtEndDt;

    @NotBlank
    private String teamId;
}