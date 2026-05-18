package com.crewise.backend.domain.mission.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "MISSION_VERIFY")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MissionVerify {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VERIFY_ID")
    private Long verifyId;

    @Column(name = "VERIFY_CONTENT")
    private String verifyContent;

    @Column(name = "VERIFY_REG_DTM", length = 19, nullable = false)
    private String verifyRegDtm;

    @Column(name = "AI_REJECT_YN", length = 1)
    private String aiRejectYn;

    @Column(name = "AI_RESULT")
    private String aiResult;

    @Column(name = "VERIFY_STATE", length = 1)
    private String verifyState;

    @Column(name = "MISSION_ID", nullable = false)
    private Long missionId;

    @Column(name = "MEM_ID", length = 26, nullable = false)
    private String memId;

    @Column(name = "REJECT_REASON")
    private String rejectReason;

    public void approve() {
        this.verifyState = "A";
    }

    public void reject(String rejectReason) {
        this.verifyState = "R";
        this.rejectReason = rejectReason;
    }
}