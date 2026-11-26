
package com.developerportal.service;

import com.developerportal.dto.GitHubRepositoryResponse;
import com.developerportal.dto.GitHubSyncResponse;
import com.developerportal.entity.CatalogService;
import com.developerportal.enums.Lifecycle;
import com.developerportal.parser.CatalogInfoParser;
import com.developerportal.repository.CatalogServiceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Service
public class GitHubSyncService {

    private final GitHubService gitHubService;
    private final CatalogInfoParser catalogInfoParser;
    private final CatalogServiceRepository catalogServiceRepository;

    public GitHubSyncService(
            GitHubService gitHubService,
            CatalogInfoParser catalogInfoParser,
            CatalogServiceRepository catalogServiceRepository) {

        this.gitHubService = gitHubService;
        this.catalogInfoParser = catalogInfoParser;
        this.catalogServiceRepository = catalogServiceRepository;
    }

    public GitHubSyncResponse synchronizeRepositories() {

        List<GitHubRepositoryResponse> repositories =
                gitHubService.getRepositories();

        int imported = 0;
        int updated = 0;
        int skipped = 0;
        int invalid = 0;

        for (GitHubRepositoryResponse repository : repositories) {

            if (repository.fullName() == null
                    || repository.fullName().isBlank()
                    || repository.htmlUrl() == null
                    || repository.htmlUrl().isBlank()) {
                invalid++;
                continue;
            }

            Optional<String> catalogContent =
                    gitHubService.getCatalogInfo(repository.fullName());

            if (catalogContent.isEmpty()) {
                skipped++;
                continue;
            }

            try {
                Map<String, Object> catalog =
                        catalogInfoParser.parse(catalogContent.get());

                Map<?, ?> metadata =
                        (Map<?, ?>) catalog.get("metadata");

                Map<?, ?> spec =
                        (Map<?, ?>) catalog.get("spec");

                String name = (String) metadata.get("name");
                String description =
                        (String) metadata.get("description");
                String owner = (String) spec.get("owner");

                String lifecycleValue =
                        ((String) spec.get("lifecycle"))
                                .toUpperCase(Locale.ROOT);

                Lifecycle lifecycle =
                        Lifecycle.valueOf(lifecycleValue);

                Optional<CatalogService> existing =
                        catalogServiceRepository.findByName(name);

                CatalogService service;

                if (existing.isPresent()) {
                    service = existing.get();
                    updated++;
                } else {
                    service = new CatalogService();
                    imported++;
                }

                service.setName(name);
                service.setDescription(description);
                service.setOwner(owner);
                service.setLifecycle(lifecycle);
                service.setRepositoryUrl(repository.htmlUrl());

                catalogServiceRepository.save(service);

            } catch (IllegalArgumentException e) {
                invalid++;
            }
        }

        return new GitHubSyncResponse(
                repositories.size(),
                imported,
                updated,
                skipped,
                invalid
        );
    }
}
