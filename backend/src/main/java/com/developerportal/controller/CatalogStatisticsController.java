
package com.developerportal.controller;

import com.developerportal.dto.CatalogStatisticsResponse;
import com.developerportal.service.CatalogStatisticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/catalog")
public class CatalogStatisticsController {

    private final CatalogStatisticsService statisticsService;

    public CatalogStatisticsController(
            CatalogStatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/statistics")
    public CatalogStatisticsResponse getStatistics() {
        return statisticsService.getStatistics();
    }
}
