package com.rescue.system.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
public class WelcomeController {

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> welcome() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Welcome to ResQOnRoad API");
        response.put("version", "1.0.0");
        response.put("description", "Rescue Service Management System");
        response.put("endpoints", Map.of(
            "auth", Map.of(
                "login", "POST /api/auth/login",
                "register", "POST /api/auth/register"
            ),
            "rescue_requests", Map.of(
                "create", "POST /api/rescue-requests",
                "get_by_id", "GET /api/rescue-requests/{id}",
                "user_requests", "GET /api/rescue-requests/user/my-requests",
                "company_requests", "GET /api/rescue-requests/company/assigned",
                "filter_by_status", "GET /api/rescue-requests/status/{status}",
                "accept", "POST /api/rescue-requests/{id}/accept",
                "update_status", "PATCH /api/rescue-requests/{id}/status",
                "reject", "POST /api/rescue-requests/{id}/reject",
                "cancel", "POST /api/rescue-requests/{id}/cancel"
            )
        ));
        response.put("documentation", "See HOW_TO_TEST_UC205.md for detailed API usage");
        return ResponseEntity.ok(response);
    }
}
