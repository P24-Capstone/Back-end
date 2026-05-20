package com.crewise.backend.domain.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardMemberResponse {

    private int streak;
    private List<TypeCount> typeCounts;
    private String topTypeLabel;
    private double avgCertHours;
    private List<RankItem> topRanks;
    private int myRank;
    private int myApprovedCount;
    private String myMemNic;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TypeCount {
        private String type;
        private String label;
        private int count;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RankItem {
        private int rank;
        private String memNic;
        private int approvedCount;
        private boolean isMe;
    }
}
