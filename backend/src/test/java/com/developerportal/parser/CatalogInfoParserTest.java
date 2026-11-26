package com.developerportal.parser;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CatalogInfoParserTest {

    private final CatalogInfoParser parser = new CatalogInfoParser();

    @Test
    void shouldParseValidCatalogInfo() {
        String yaml = """
                apiVersion: backstage.io/v1alpha1
                kind: Component
                metadata:
                  name: payment-service
                  description: Handles payments
                spec:
                  owner: payments-team
                  lifecycle: production
                """;

        Map<String, Object> result = parser.parse(yaml);

        assertEquals("Component", result.get("kind"));

        Map<?, ?> metadata = (Map<?, ?>) result.get("metadata");
        assertEquals("payment-service", metadata.get("name"));
    }

    @Test
    void shouldRejectMissingMetadata() {
        String yaml = """
                apiVersion: backstage.io/v1alpha1
                kind: Component
                spec:
                  owner: payments-team
                  lifecycle: production
                """;

        assertThrows(
                IllegalArgumentException.class,
                () -> parser.parse(yaml)
        );
    }

    @Test
    void shouldRejectMissingRequiredField() {
        String yaml = """
                apiVersion: backstage.io/v1alpha1
                kind: Component
                metadata:
                  name: payment-service
                spec:
                  owner: payments-team
                  lifecycle: production
                """;

        assertThrows(
                IllegalArgumentException.class,
                () -> parser.parse(yaml)
        );
    }
}