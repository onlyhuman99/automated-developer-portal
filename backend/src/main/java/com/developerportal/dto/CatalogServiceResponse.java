
package com.developerportal.dto;

import com.developerportal.entity.CatalogService;
import com.developerportal.enums.Lifecycle;

import java.time.LocalDateTime;
import java.util.UUID;

public record CatalogServiceResponse(
        UUID id,
        String name,
        String description,
        String repositoryUrl,
        String owner,
        Lifecycle lifecycle,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static CatalogServiceResponse from(CatalogService service) {
        return new CatalogServiceResponse(
                service.getId(),
                service.getName(),
                service.getDescription(),
                service.getRepositoryUrl(),
                service.getOwner(),
                service.getLifecycle(),
                service.getCreatedAt(),
                service.getUpdatedAt()
        );
    }
}
