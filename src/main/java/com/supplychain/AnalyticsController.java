package com.supplychain;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping
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

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP", "service", "supply-chain-analytics");
    }

    @GetMapping("/carriers")
    public List<Map<String, String>> carriers() throws IOException {
        return analyticsService.carrierSummary();
    }

    @GetMapping("/warehouses/bottlenecks")
    public List<Map<String, String>> bottlenecks() throws IOException {
        return analyticsService.warehouseBottlenecks();
    }

    @GetMapping("/shipments/high-risk")
    public List<Map<String, String>> highRiskShipments() throws IOException {
        return analyticsService.highRiskShipments();
    }

    @GetMapping("/shipments/{orderId}")
    public Map<String, String> shipment(@PathVariable String orderId) throws IOException {
        return analyticsService.shipment(orderId)
                .orElseThrow(() -> new ShipmentNotFoundException(orderId));
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    private static class ShipmentNotFoundException extends RuntimeException {
        ShipmentNotFoundException(String orderId) {
            super("Shipment not found: " + orderId);
        }
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(IOException.class)
    public Map<String, String> reportUnavailable(IOException exception) {
        return Map.of("error", exception.getMessage());
    }
}
