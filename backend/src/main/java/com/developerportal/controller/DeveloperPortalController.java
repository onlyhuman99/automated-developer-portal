package com.developerportal.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class DeveloperPortalController {

    @GetMapping("/hello")
    public Map<String, String> hello() {
        return Map.of(
            "message", "Welcome to the Developer Portal API",
            "status", "success"
        );
    }
}