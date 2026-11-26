package com.developerportal.repository;

import com.developerportal.entity.CatalogService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CatalogServiceRepository
        extends JpaRepository<CatalogService, UUID> {
}