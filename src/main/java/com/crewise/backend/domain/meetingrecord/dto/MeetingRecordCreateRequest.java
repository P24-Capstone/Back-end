package com.crewise.backend.domain.meetingrecord.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeetingRecordCreateRequest {

    @NotBlank
    private String teamId;

    private String recFileKey;
}