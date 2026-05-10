package com.crewise.backend.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SignupRequest {

    @Email
    @NotBlank
    private String userEmail;

    @NotBlank
    private String userPw;

    @NotBlank
    private String userName;

    @NotBlank
    private String userTel;
}