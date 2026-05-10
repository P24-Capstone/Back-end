package com.crewise.backend.domain.notice.dto;

import com.crewise.backend.domain.notice.entity.Notice;
import lombok.Getter;

@Getter
public class NoticeResponse {

    private Long notiId;
    private String notiTitle;
    private String notiContent;
    private String notiFix;
    private String regDtm;
    private String modDtm;
    private String teamId;

    public static NoticeResponse from(Notice notice) {
        NoticeResponse response = new NoticeResponse();
        response.notiId = notice.getNotiId();
        response.notiTitle = notice.getNotiTitle();
        response.notiContent = notice.getNotiContent();
        response.notiFix = notice.getNotiFix();
        response.regDtm = notice.getRegDtm();
        response.modDtm = notice.getModDtm();
        response.teamId = notice.getTeamId();
        return response;
    }
}