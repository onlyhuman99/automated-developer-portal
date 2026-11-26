
package com.developerportal.service;

import com.developerportal.dto.GitHubFileResponse;
import com.developerportal.dto.GitHubRepositoryResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

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

    public Optional<String> getCatalogInfo(String fullName) {

        String[] parts = fullName.split("/", 2);

        if (parts.length != 2
                || parts[0].isBlank()
                || parts[1].isBlank()) {
            throw new IllegalArgumentException(
                    "Repository must be in owner/repository format"
            );
        }

        try {
            GitHubFileResponse response = restClient.get()
                    .uri(
                            "/repos/{owner}/{repo}/contents/catalog-info.yaml",
                            parts[0],
                            parts[1]
                    )
                    .retrieve()
                    .body(GitHubFileResponse.class);

            if (response == null
                    || response.content() == null
                    || !"base64".equals(response.encoding())) {
                return Optional.empty();
            }

            byte[] decoded = Base64.getMimeDecoder()
                    .decode(response.content());

            return Optional.of(
                    new String(decoded, StandardCharsets.UTF_8)
            );

        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        }
    }
}
