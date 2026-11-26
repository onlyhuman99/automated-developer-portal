
package com.developerportal.repository;

import com.developerportal.entity.CatalogService;
import com.developerportal.enums.Lifecycle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CatalogServiceRepository
        extends JpaRepository<CatalogService, UUID> {

    Optional<CatalogService> findByName(String name);

    @Query("""
            SELECT s.lifecycle, COUNT(s)
            FROM CatalogService s
            GROUP BY s.lifecycle
            """)
    List<Object[]> countServicesByLifecycle();

    @Query("""
            SELECT s.owner, COUNT(s)
            FROM CatalogService s
            GROUP BY s.owner
            """)
    List<Object[]> countServicesByOwner();
}
