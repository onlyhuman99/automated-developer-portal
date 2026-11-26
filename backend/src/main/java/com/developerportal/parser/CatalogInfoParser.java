
package com.developerportal.parser;

import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import java.util.Map;

@Component
public class CatalogInfoParser {

    private final Yaml yaml;

    public CatalogInfoParser() {
        LoaderOptions options = new LoaderOptions();
        this.yaml = new Yaml(new SafeConstructor(options));
    }

    public Map<String, Object> parse(String content) {
        Object result = yaml.load(content);

        if (!(result instanceof Map<?, ?> root)) {
            throw new IllegalArgumentException(
                    "Invalid catalog-info.yaml format"
            );
        }

        Object metadataObject = root.get("metadata");
        Object specObject = root.get("spec");

        if (!(metadataObject instanceof Map<?, ?> metadata)
                || !(specObject instanceof Map<?, ?> spec)) {
            throw new IllegalArgumentException(
                    "Missing metadata or spec section"
            );
        }

        requireField(metadata, "name");
        requireField(metadata, "description");
        requireField(spec, "owner");
        requireField(spec, "lifecycle");

        return (Map<String, Object>) root;
    }

    private void requireField(Map<?, ?> section, String field) {
        Object value = section.get(field);

        if (!(value instanceof String text) || text.isBlank()) {
            throw new IllegalArgumentException(
                    "Missing or invalid required field: " + field
            );
        }
    }
}
