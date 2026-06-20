package com.reservas.sistema_reservas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.reservas.sistema_reservas.dto.DashboardDTO;
import com.reservas.sistema_reservas.service.DashboardService;

@RestController
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/api/dashboard")
    public DashboardDTO obtenerDashboard() {
        return dashboardService.obtenerDashboard();
    }
}
