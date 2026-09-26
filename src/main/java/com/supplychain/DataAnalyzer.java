package com.supplychain;

import tech.tablesaw.api.*;
import tech.tablesaw.aggregate.AggregateFunctions;
import java.io.IOException;

public class DataAnalyzer {

    public static void processAndAnalyze(String csvFilePath) throws IOException {
        // Load dataset using Tablesaw
        Table table = Table.read().csv(csvFilePath);

        System.out.println("\n--- Dataset Summary ---");
        System.out.println(table.shape());
        System.out.println("\n--- First 5 Rows ---");
        System.out.println(table.first(5));

        // Aggregate delayed shipments by carrier, then derive the OTD percentage.
        Table carrierSummary = table.summarize("is_delayed", AggregateFunctions.mean)
                                    .by("carrier_id");
        carrierSummary.column(1).setName("delay_rate");
        DoubleColumn otdPercent = DoubleColumn.create("otd_percent");
        DoubleColumn delayRate = carrierSummary.doubleColumn("delay_rate");
        for (int i = 0; i < delayRate.size(); i++) {
            otdPercent.append((1.0 - delayRate.get(i)) * 100.0);
        }
        carrierSummary.addColumns(otdPercent);

        System.out.println("\n--- Carrier Performance (OTD %) ---");
        System.out.println(carrierSummary);

        Table warehouseBottlenecks = table.summarize("processing_time_hours", AggregateFunctions.mean)
                                           .by("warehouse_id")
                                           .sortOn("-Mean [processing_time_hours]");
        warehouseBottlenecks.column(1).setName("average_processing_time_hours");
        System.out.println("\n--- Warehouse Processing Bottlenecks ---");
        System.out.println(warehouseBottlenecks);

        // Save carrier summary CSV
        carrierSummary.write().csv("data/carrier_summary.csv");
        warehouseBottlenecks.write().csv("data/warehouse_bottlenecks.csv");
        System.out.println("\nSummary metrics saved to 'data/carrier_summary.csv'");
        System.out.println("Bottleneck metrics saved to 'data/warehouse_bottlenecks.csv'");
    }
}