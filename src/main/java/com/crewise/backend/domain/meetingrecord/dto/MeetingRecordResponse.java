package com.crewise.backend.domain.meetingrecord.dto;

import com.crewise.backend.domain.meetingrecord.entity.MeetingRecord;
import lombok.Getter;

@Getter
public class MeetingRecordResponse {

    private Long meetingId;
    private String meetingTitle;
    private String fullScript;
    private String aiSummary;
    private String regDtm;
    private String teamId;
    private String recFileKey;

    public static MeetingRecordResponse from(MeetingRecord record, String recFileKey) {
        MeetingRecordResponse response = new MeetingRecordResponse();
        response.meetingId = record.getMeetingId();
        response.meetingTitle = record.getMeetingTitle();
        response.fullScript = record.getFullScript();
        response.aiSummary = record.getAiSummary();
        response.regDtm = record.getRegDtm();
        response.teamId = record.getTeamId();
        response.recFileKey = recFileKey;
        return response;
    }
}