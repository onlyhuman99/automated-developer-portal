package com.developerportal.dto;

import com.developerportal.enums.Lifecycle;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

public record CatalogServiceRequest(

    @NotBlank
    @Size(max = 100)
    String name,

    @Size(max = 500)
    String description,

    @NotBlank
    @Size(max = 500)
    @Pattern(regexp = "^https://github\\.com/[^/]+/[^/]+/?$",
            message = "Must be a valid GitHub repository URL")
    String repositoryUrl,

    @NotBlank
    @Size(max = 100)
    String owner,

    @NotNull
    Lifecycle lifecycle

) {}