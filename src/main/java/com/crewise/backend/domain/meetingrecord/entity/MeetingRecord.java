package com.crewise.backend.domain.meetingrecord.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "MEETING_RECORD")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MeetingRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEETING_ID")
    private Long meetingId;

    @Column(name = "MEETING_TITLE", nullable = false)
    private String meetingTitle;

    @Column(name = "FULL_SCRIPT", nullable = false)
    private String fullScript;

    @Column(name = "AI_SUMMARY", nullable = false)
    private String aiSummary;

    @Column(name = "REG_DTM", length = 19, nullable = false)
    private String regDtm;

    @Column(name = "TEAM_ID", length = 10, nullable = false)
    private String teamId;
}