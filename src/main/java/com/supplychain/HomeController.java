package com.supplychain;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, Object> overview() {
        return Map.of(
                "service", "supply-chain-analytics",
                "status", "UP",
                "message", "Supply Chain Analytics API is running",
                "endpoints", List.of(
                        "/api/health",
                        "/api/carriers",
                        "/api/warehouses/bottlenecks",
                        "/api/shipments/high-risk",
                        "/api/shipments/{orderId}"));
    }
}
