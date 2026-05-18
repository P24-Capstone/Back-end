package com.crewise.backend.domain.mission.dto;

import com.crewise.backend.domain.mission.entity.MissionVerify;
import lombok.Getter;

import java.util.List;

@Getter
public class MissionVerifyResponse {

    private Long verifyId;
    private String verifyContent;
    private String verifyRegDtm;
    private String aiRejectYn;
    private String aiResult;
    private String verifyState;
    private Long missionId;
    private String memId;
    private String memNic;
    private String rejectReason;
    private List<String> fileKeys;

    public static MissionVerifyResponse from(MissionVerify verify, String memNic, List<String> fileKeys) {
        MissionVerifyResponse r = new MissionVerifyResponse();
        r.verifyId = verify.getVerifyId();
        r.verifyContent = verify.getVerifyContent();
        r.verifyRegDtm = verify.getVerifyRegDtm();
        r.aiRejectYn = verify.getAiRejectYn();
        r.aiResult = verify.getAiResult();
        r.verifyState = verify.getVerifyState();
        r.missionId = verify.getMissionId();
        r.memId = verify.getMemId();
        r.memNic = memNic;
        r.rejectReason = verify.getRejectReason();
        r.fileKeys = fileKeys;
        return r;
    }
}