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
public class DashboardLeaderResponse {

    private int totalMissions;
    private int totalMembers;
    private double overallAchievementRate;
    private List<WeeklyPoint> weeklyTrend;
    private List<HardMission> hardMissions;
    private int[][] heatmap;
    private double aiSuccessRate;
    private double fallbackRate;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeeklyPoint {
        private String weekLabel;
        private double rate;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HardMission {
        private String missionTitle;
        private double failRate;
    }
}
