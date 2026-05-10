package com.crewise.backend.domain.member.dto;

import com.crewise.backend.domain.member.entity.MemberSignup;
import lombok.Getter;

@Getter
public class MemberSignupResponse {

    private Long signupId;
    private String teamId;
    private String userId;
    private String signState;
    private String regDtm;
    private String procDtm;

    public static MemberSignupResponse from(MemberSignup signup) {
        MemberSignupResponse response = new MemberSignupResponse();
        response.signupId = signup.getSignupId();
        response.teamId = signup.getTeamId();
        response.userId = signup.getUserId();
        response.signState = signup.getSignState();
        response.regDtm = signup.getRegDtm();
        response.procDtm = signup.getProcDtm();
        return response;
    }
}