
package com.developerportal.dto;

public record GitHubSyncResponse(
        int scanned,
        int imported,
        int updated,
        int skipped,
        int invalid
) {}
