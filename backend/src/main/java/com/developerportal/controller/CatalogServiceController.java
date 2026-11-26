
package com.developerportal.controller;

import com.developerportal.dto.CatalogServiceRequest;
import com.developerportal.dto.CatalogServiceResponse;
import com.developerportal.entity.CatalogService;
import com.developerportal.service.CatalogServiceService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/services")
public class CatalogServiceController {

    private final CatalogServiceService catalogServiceService;

    public CatalogServiceController(
            CatalogServiceService catalogServiceService) {
        this.catalogServiceService = catalogServiceService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CatalogServiceResponse createService(
            @Valid @RequestBody CatalogServiceRequest request) {

        CatalogService service = new CatalogService();
        service.setName(request.name());
        service.setDescription(request.description());
        service.setRepositoryUrl(request.repositoryUrl());
        service.setOwner(request.owner());
        service.setLifecycle(request.lifecycle());

        CatalogService savedService =
                catalogServiceService.createService(service);

        return CatalogServiceResponse.from(savedService);
    }

    @GetMapping
    public List<CatalogServiceResponse> getAllServices() {

        return catalogServiceService.getAllServices()
                .stream()
                .map(CatalogServiceResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public CatalogServiceResponse getServiceById(
            @PathVariable UUID id) {

        return CatalogServiceResponse.from(
                catalogServiceService.getServiceById(id)
        );
    }

    @PutMapping("/{id}")
    public CatalogServiceResponse updateService(
            @PathVariable UUID id,
            @Valid @RequestBody CatalogServiceRequest request) {

        CatalogService updatedService = new CatalogService();
        updatedService.setName(request.name());
        updatedService.setDescription(request.description());
        updatedService.setRepositoryUrl(request.repositoryUrl());
        updatedService.setOwner(request.owner());
        updatedService.setLifecycle(request.lifecycle());

        CatalogService savedService =
                catalogServiceService.updateService(id, updatedService);

        return CatalogServiceResponse.from(savedService);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteService(@PathVariable UUID id) {
        catalogServiceService.deleteService(id);
    }
}
