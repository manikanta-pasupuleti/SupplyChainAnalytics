package com.supplychain;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AnalyticsService {

    private static final Path CARRIER_SUMMARY = Path.of("data/carrier_summary.csv");
    private static final Path WAREHOUSE_BOTTLENECKS = Path.of("data/warehouse_bottlenecks.csv");
    private static final Path PREDICTIONS = Path.of("data/supply_chain_predictions.csv");

    public List<Map<String, String>> carrierSummary() throws IOException {
        return readCsv(CARRIER_SUMMARY);
    }

    public List<Map<String, String>> warehouseBottlenecks() throws IOException {
        return readCsv(WAREHOUSE_BOTTLENECKS);
    }

    public List<Map<String, String>> highRiskShipments() throws IOException {
        return readCsv(PREDICTIONS).stream()
                .filter(row -> "High Risk".equals(row.get("risk_tier")))
                .toList();
    }

    public Optional<Map<String, String>> shipment(String orderId) throws IOException {
        return readCsv(PREDICTIONS).stream()
                .filter(row -> orderId.equals(row.get("order_id")))
                .findFirst();
    }

    private List<Map<String, String>> readCsv(Path path) throws IOException {
        if (!Files.exists(path)) {
            throw new IOException("Analytics report not found: " + path);
        }
        List<String> lines = Files.readAllLines(path);
        if (lines.isEmpty()) {
            return List.of();
        }
        String[] headers = lines.get(0).split(",", -1);
        List<Map<String, String>> rows = new ArrayList<>();
        for (String line : lines.subList(1, lines.size())) {
            if (line.isBlank()) {
                continue;
            }
            String[] values = line.split(",", -1);
            Map<String, String> row = new LinkedHashMap<>();
            for (int i = 0; i < headers.length; i++) {
                row.put(headers[i], i < values.length ? values[i] : "");
            }
            rows.add(row);
        }
        return rows;
    }
}
