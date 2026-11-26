
package com.developerportal.dto;

import com.developerportal.enums.Lifecycle;

import java.util.Map;

public record CatalogStatisticsResponse(
        long totalServices,
        Map<Lifecycle, Long> servicesByLifecycle,
        Map<String, Long> servicesByOwner
) {}
