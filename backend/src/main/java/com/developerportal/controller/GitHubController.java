package com.developerportal.controller;

import com.developerportal.dto.GitHubRepositoryResponse;
import com.developerportal.service.GitHubService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/github")
public class GitHubController {

    private final GitHubService gitHubService;

    public GitHubController(GitHubService gitHubService) {
        this.gitHubService = gitHubService;
    }

    @GetMapping("/repositories")
    public List<GitHubRepositoryResponse> getRepositories() {
        return gitHubService.getRepositories();
    }
}