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
    private Long userImgId;
    private String imgFileKey;

    public static MemberResponse from(Member member, String imgFileKey) {
        MemberResponse response = new MemberResponse();
        response.memId = member.getMemId();
        response.memNic = member.getMemNic();
        response.memRole = member.getMemRole();
        response.memState = member.getMemState();
        response.regDtm = member.getRegDtm();
        response.procDtm = member.getProcDtm();
        response.userId = member.getUserId();
        response.teamId = member.getTeamId();
        response.userImgId = member.getUserImgId();
        response.imgFileKey = imgFileKey;
        return response;
    }

    // 이미지 없는 경우
    public static MemberResponse from(Member member) {
        return from(member, null);
    }
}