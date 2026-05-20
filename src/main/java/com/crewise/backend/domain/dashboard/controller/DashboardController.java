package com.crewise.backend.domain.dashboard.controller;

import com.crewise.backend.domain.dashboard.dto.DashboardSummaryResponse;
import com.crewise.backend.domain.dashboard.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    public DashboardSummaryResponse getSummary() {
        // In a real app, the user ID would be resolved from the authentication token.
        // For now we simply delegate to the service which returns placeholder data.
        return dashboardService.getDashboardSummary();
    }
}
