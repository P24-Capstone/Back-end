package com.crewise.backend.domain.mission.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "MISSION")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Mission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MISSION_ID")
    private Long missionId;

    @Column(name = "MISSION_TITLE", length = 100, nullable = false)
    private String missionTitle;

    @Column(name = "MISSION_CONTENT", nullable = false)
    private String missionContent;

    @Column(name = "MISSION_TYPE", length = 1, nullable = false)
    private String missionType;

    @Column(name = "VERIFY_PROMPT")
    private String verifyPrompt;

    @Column(name = "MISSION_START_DTM", length = 19, nullable = false)
    private String missionStartDtm;

    @Column(name = "MISSION_END_DTM", length = 19, nullable = false)
    private String missionEndDtm;

    @Column(name = "TEAM_ID", length = 10, nullable = false)
    private String teamId;
}