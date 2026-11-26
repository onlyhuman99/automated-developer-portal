
package com.developerportal.controller;

import com.developerportal.dto.GitHubRepositoryResponse;
import com.developerportal.dto.GitHubSyncResponse;
import com.developerportal.service.GitHubService;
import com.developerportal.service.GitHubSyncService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/github")
public class GitHubController {

    private final GitHubService gitHubService;
    private final GitHubSyncService gitHubSyncService;

    public GitHubController(
            GitHubService gitHubService,
            GitHubSyncService gitHubSyncService) {

        this.gitHubService = gitHubService;
        this.gitHubSyncService = gitHubSyncService;
    }

    @GetMapping("/repositories")
    public List<GitHubRepositoryResponse> getRepositories() {
        return gitHubService.getRepositories();
    }

    @PostMapping("/sync")
    public GitHubSyncResponse synchronizeRepositories() {
        return gitHubSyncService.synchronizeRepositories();
    }
}
