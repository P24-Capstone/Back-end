package com.crewise.backend.domain.event.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
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