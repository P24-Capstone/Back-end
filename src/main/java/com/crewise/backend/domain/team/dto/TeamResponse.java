package com.crewise.backend.domain.team.dto;

import com.crewise.backend.domain.team.entity.Team;
import lombok.Getter;

@Getter
public class TeamResponse {

    private String teamId;
    private String teamName;
    private String teamImg;
    private String teamInfo;
    private String teamCategory;
    private Integer currentMember;
    private Integer maxMembers;
    private String code;

    public static TeamResponse from(Team team) {
        TeamResponse response = new TeamResponse();
        response.teamId = team.getTeamId();
        response.teamName = team.getTeamName();
        response.teamImg = team.getTeamImg();
        response.teamInfo = team.getTeamInfo();
        response.teamCategory = team.getTeamCategory();
        response.currentMember = team.getCurrentMember();
        response.maxMembers = team.getMaxMembers();
        response.code = team.getCode();
        return response;
    }
}