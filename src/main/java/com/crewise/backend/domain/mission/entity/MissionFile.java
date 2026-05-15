package com.crewise.backend.domain.mission.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "MISSION_FILE")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MissionFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MISSION_FILE_SN")
    private Long missionFileSn;

    @Column(name = "MISSION_FILE_KEY", length = 1024, nullable = false)
    private String missionFileKey;

    @Column(name = "MISSION_ID", nullable = false)
    private Long missionId;
}