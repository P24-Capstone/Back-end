package com.crewise.backend.domain.dashboard.dto;

public class DashboardSummaryResponse {
    private double averageActivityHours;
    private int missionRank;
    private int weeklyProgressPercent;

    public DashboardSummaryResponse() {}

    public DashboardSummaryResponse(double averageActivityHours, int missionRank, int weeklyProgressPercent) {
        this.averageActivityHours = averageActivityHours;
        this.missionRank = missionRank;
        this.weeklyProgressPercent = weeklyProgressPercent;
    }

    public double getAverageActivityHours() {
        return averageActivityHours;
    }
    public void setAverageActivityHours(double averageActivityHours) {
        this.averageActivityHours = averageActivityHours;
    }
    public int getMissionRank() {
        return missionRank;
    }
    public void setMissionRank(int missionRank) {
        this.missionRank = missionRank;
    }
    public int getWeeklyProgressPercent() {
        return weeklyProgressPercent;
    }
    public void setWeeklyProgressPercent(int weeklyProgressPercent) {
        this.weeklyProgressPercent = weeklyProgressPercent;
    }
}
