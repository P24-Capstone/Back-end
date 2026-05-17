package com.crewise.backend.domain.user.dto;

import com.crewise.backend.domain.user.entity.UserImg;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserImgResponse {

    private Long imgId;
    private String imgFileKey;

    public static UserImgResponse from(UserImg userImg) {
        return new UserImgResponse(userImg.getImgId(), userImg.getImgFileKey());
    }
}
