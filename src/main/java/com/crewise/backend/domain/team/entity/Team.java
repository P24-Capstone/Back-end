package com.crewise.backend.domain.team.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TEAM")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Team {

    @Id
    @Column(name = "TEAM_ID", length = 10)
    private String teamId;

    @Column(name = "TEAM_NAME", length = 50, nullable = false)
    private String teamName;

    @Column(name = "TEAM_IMG", length = 1024)
    private String teamImg;

    @Column(name = "TEAM_INFO")
    private String teamInfo;

    @Column(name = "TEAM_CATEGORY", length = 20)
    private String teamCategory;

    @Column(name = "CURRENT_MEMBERS")
    private Integer currentMember;

    @Column(name = "MAX_MEMBERS")
    private Integer maxMembers;

    @Column(name = "CODE", length = 10)
    private String code;
}