package com.crewise.backend.domain.dashboard.service;

import com.crewise.backend.domain.dashboard.dto.DashboardSummaryResponse;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    // In a real implementation, you would inject repositories to compute these values.
    // For now we return placeholder values.
    public DashboardSummaryResponse getDashboardSummary() {
        // Placeholder: average activity hours per day, mission rank, weekly progress percent.
        double averageActivityHours = 4.2; // e.g., 4.2 hours per day
        int missionRank = 12; // e.g., 12th rank among users
        int weeklyProgressPercent = 73; // e.g., 73% of weekly missions completed
        return new DashboardSummaryResponse(averageActivityHours, missionRank, weeklyProgressPercent);
    }
}
