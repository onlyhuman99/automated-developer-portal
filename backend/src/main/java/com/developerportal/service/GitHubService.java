package com.developerportal.service;

import com.developerportal.dto.GitHubRepositoryResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class GitHubService {

    private final RestClient restClient;

    public GitHubService(
            @Value("${github.api.base-url}") String baseUrl,
            @Value("${github.token}") String token) {

        if (token == null || token.isBlank()) {
            throw new IllegalStateException(
                    "GITHUB_TOKEN environment variable is not configured"
            );
        }

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + token
                )
                .defaultHeader(
                        HttpHeaders.ACCEPT,
                        "application/vnd.github+json"
                )
                .defaultHeader(
                        "X-GitHub-Api-Version",
                        "2022-11-28"
                )
                .build();
    }

    public List<GitHubRepositoryResponse> getRepositories() {

        return restClient.get()
                .uri("/user/repos?affiliation=owner&per_page=100&sort=updated")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}