package com.crewise.backend.domain.member.dto;

import com.crewise.backend.domain.member.entity.Member;
import lombok.Getter;

@Getter
public class MemberResponse {

    private String memId;
    private String memNic;
    private String memRole;
    private String memState;
    private String regDtm;
    private String procDtm;
    private String userId;
    private String teamId;

    public static MemberResponse from(Member member) {
        MemberResponse response = new MemberResponse();
        response.memId = member.getMemId();
        response.memNic = member.getMemNic();
        response.memRole = member.getMemRole();
        response.memState = member.getMemState();
        response.regDtm = member.getRegDtm();
        response.procDtm = member.getProcDtm();
        response.userId = member.getUserId();
        response.teamId = member.getTeamId();
        return response;
    }
}