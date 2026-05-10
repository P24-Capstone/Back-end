package com.crewise.backend.domain.event.dto;

import com.crewise.backend.domain.event.entity.Event;
import lombok.Getter;

@Getter
public class EventResponse {

    private Long evtId;
    private String evtTitle;
    private String evtContent;
    private String evtStartDt;
    private String evtEndDt;
    private String evtRegDtm;
    private String teamId;

    public static EventResponse from(Event event) {
        EventResponse response = new EventResponse();
        response.evtId = event.getEvtId();
        response.evtTitle = event.getEvtTitle();
        response.evtContent = event.getEvtContent();
        response.evtStartDt = event.getEvtStartDt();
        response.evtEndDt = event.getEvtEndDt();
        response.evtRegDtm = event.getEvtRegDtm();
        response.teamId = event.getTeamId();
        return response;
    }
}