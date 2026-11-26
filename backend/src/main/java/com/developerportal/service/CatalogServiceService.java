package com.developerportal.service;

import com.developerportal.entity.CatalogService;
import com.developerportal.repository.CatalogServiceRepository;

import com.developerportal.exception.ServiceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CatalogServiceService {

    private final CatalogServiceRepository repository;
    public CatalogService updateService(UUID id, CatalogService updated) {
        CatalogService existing = getServiceById(id);

        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setRepositoryUrl(updated.getRepositoryUrl());
        existing.setOwner(updated.getOwner());
        existing.setLifecycle(updated.getLifecycle());

        return repository.save(existing);
    }

    public CatalogServiceService(CatalogServiceRepository repository) {
        this.repository = repository;
    }

    public CatalogService createService(CatalogService service) {
        return repository.save(service);
    }

    public List<CatalogService> getAllServices() {
        return repository.findAll();
    }

    public CatalogService getServiceById(UUID id) {
    return repository.findById(id)
            .orElseThrow(() ->
                new ServiceNotFoundException(
                    "Service not found: " + id
                )
            );
    }

    public void deleteService(UUID id) {
    if (!repository.existsById(id)) {
        throw new ServiceNotFoundException(
            "Service not found: " + id
        );
    }
    
    
        repository.deleteById(id);
    }
}