package com.crewise.backend.domain.mission.dto;

import com.crewise.backend.domain.mission.entity.Mission;
import lombok.Getter;

@Getter
public class MissionResponse {

    private Long missionId;
    private String missionTitle;
    private String missionContent;
    private String missionType;
    private String verifyPrompt;
    private String missionStartDtm;
    private String missionEndDtm;
    private String teamId;

    public static MissionResponse from(Mission mission) {
        MissionResponse response = new MissionResponse();
        response.missionId = mission.getMissionId();
        response.missionTitle = mission.getMissionTitle();
        response.missionContent = mission.getMissionContent();
        response.missionType = mission.getMissionType();
        response.verifyPrompt = mission.getVerifyPrompt();
        response.missionStartDtm = mission.getMissionStartDtm();
        response.missionEndDtm = mission.getMissionEndDtm();
        response.teamId = mission.getTeamId();
        return response;
    }
}