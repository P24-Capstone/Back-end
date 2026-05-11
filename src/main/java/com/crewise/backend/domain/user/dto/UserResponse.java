package com.crewise.backend.domain.user.dto;

import com.crewise.backend.domain.user.entity.User;
import lombok.Getter;

@Getter
public class UserResponse {

    private String userId;
    private String userEmail;
    private String userName;
    private String userTel;

    public static UserResponse from(User user) {
        UserResponse response = new UserResponse();
        response.userId = user.getUserId();
        response.userEmail = user.getUserEmail();
        response.userName = user.getUserName();
        response.userTel = user.getUserTel();
        return response;
    }
}
