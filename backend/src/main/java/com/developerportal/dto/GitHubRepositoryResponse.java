package com.developerportal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

public record GitHubRepositoryResponse(
        String name,

        @JsonProperty("full_name")
        String fullName,

        @JsonProperty("html_url")
        String htmlUrl,

        String description,

        boolean fork,

        @JsonProperty("default_branch")
        String defaultBranch,

        @JsonProperty("updated_at")
        OffsetDateTime updatedAt
) {}