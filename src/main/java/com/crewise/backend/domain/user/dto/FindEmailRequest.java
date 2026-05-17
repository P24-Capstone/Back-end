package com.crewise.backend.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class FindEmailRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String phone;
}
