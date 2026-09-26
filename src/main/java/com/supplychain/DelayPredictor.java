package com.supplychain;

import tech.tablesaw.api.*;
import java.io.IOException;

public class DelayPredictor {

    public static void trainAndPredict(String csvPath) throws IOException {
        Table table = Table.read().csv(csvPath);

        System.out.println("\n==================================================");
        System.out.println("      JAVA SUPPLY CHAIN DELAY PREDICTION ML       ");
        System.out.println("==================================================");

        // Feature Engineering: Calculate Risk Score metric
        DoubleColumn distance = table.doubleColumn("shipping_distance_km");
        IntColumn processingTime = table.intColumn("processing_time_hours");
        DoubleColumn supplierScore = table.doubleColumn("supplier_reliability_score");

        // Simple Heuristic Risk Score Engine in Java
        DoubleColumn riskScoreCol = DoubleColumn.create("risk_score");

        for (int i = 0; i < table.rowCount(); i++) {
            double dist = distance.get(i);
            int proc = processingTime.get(i);
            double supp = supplierScore.get(i);

            // Compute composite risk score (0.0 to 1.0)
            double score = (dist / 2500.0) * 0.35 + (proc / 72.0) * 0.35 + (1.0 - supp) * 0.30;
            riskScoreCol.append(Math.round(score * 100.0) / 100.0);
        }

        table.addColumns(riskScoreCol);
        StringColumn riskTierCol = StringColumn.create("risk_tier");
        for (int i = 0; i < riskScoreCol.size(); i++) {
            riskTierCol.append(riskScoreCol.get(i) > 0.50 ? "High Risk" : "Low Risk");
        }
        table.addColumns(riskTierCol);

        // Filter high risk shipments (> 0.50 score)
        Table highRiskShipments = table.where(table.doubleColumn("risk_score").isGreaterThan(0.50));

        System.out.println("Total Orders Evaluated: " + table.rowCount());
        System.out.println("High Risk Delay Shipments Identified: " + highRiskShipments.rowCount());
        System.out.println("\n--- Top 5 High Risk Shipments ---");
        System.out.println(highRiskShipments.first(5));

        // Save prediction output for Dashboard / Reporting
        table.write().csv("data/supply_chain_predictions.csv");
        System.out.println("\nPrediction results saved to 'data/supply_chain_predictions.csv'");
    }
}