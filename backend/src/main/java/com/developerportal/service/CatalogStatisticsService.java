
package com.developerportal.service;

import com.developerportal.dto.CatalogStatisticsResponse;
import com.developerportal.enums.Lifecycle;
import com.developerportal.repository.CatalogServiceRepository;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class CatalogStatisticsService {

    private final CatalogServiceRepository repository;

    public CatalogStatisticsService(
            CatalogServiceRepository repository) {
        this.repository = repository;
    }

    public CatalogStatisticsResponse getStatistics() {

        long totalServices = repository.count();

        Map<Lifecycle, Long> servicesByLifecycle =
                new EnumMap<>(Lifecycle.class);

        for (Lifecycle lifecycle : Lifecycle.values()) {
            servicesByLifecycle.put(lifecycle, 0L);
        }

        for (Object[] result : repository.countServicesByLifecycle()) {
            Lifecycle lifecycle = (Lifecycle) result[0];
            Long count = ((Number) result[1]).longValue();

            servicesByLifecycle.put(lifecycle, count);
        }

        Map<String, Long> servicesByOwner = new LinkedHashMap<>();

        for (Object[] result : repository.countServicesByOwner()) {
            String owner = (String) result[0];
            Long count = ((Number) result[1]).longValue();

            servicesByOwner.put(owner, count);
        }

        return new CatalogStatisticsResponse(
                totalServices,
                servicesByLifecycle,
                servicesByOwner
        );
    }
}
