package com.supplychain;

import tech.tablesaw.api.*;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class DataGenerator {

    public static String generateSupplyChainData(int totalRecords) throws IOException {
        Random rand = new Random(42);

        String[] carriers = {"DHL", "FedEx", "UPS", "USPS", "Amazon Logistics"};
        String[] warehouses = {"WH-Alpha (US-East)", "WH-Beta (US-West)", "WH-Gamma (US-Central)", "WH-Delta (US-South)"};
        String[] shippingModes = {"Standard Ground", "Express Air", "Same-Day", "Two-Day Air"};

        StringColumn orderIdCol = StringColumn.create("order_id");
        StringColumn carrierCol = StringColumn.create("carrier_id");
        StringColumn warehouseCol = StringColumn.create("warehouse_id");
        StringColumn shippingModeCol = StringColumn.create("shipping_mode");
        DoubleColumn supplierScoreCol = DoubleColumn.create("supplier_reliability_score");
        IntColumn inventoryCol = IntColumn.create("warehouse_inventory_level");
        DoubleColumn distanceCol = DoubleColumn.create("shipping_distance_km");
        IntColumn processingTimeCol = IntColumn.create("processing_time_hours");
        IntColumn isDelayedCol = IntColumn.create("is_delayed");

        for (int i = 0; i < totalRecords; i++) {
            orderIdCol.append("ORD-" + (1000 + i));
            carrierCol.append(carriers[rand.nextInt(carriers.length)]);
            warehouseCol.append(warehouses[rand.nextInt(warehouses.length)]);
            shippingModeCol.append(shippingModes[rand.nextInt(shippingModes.length)]);

            double supplierScore = Math.round((0.5 + rand.nextDouble() * 0.5) * 100.0) / 100.0;
            int inventory = 10 + rand.nextInt(4990);
            double distance = Math.round((20.0 + rand.nextDouble() * 2480.0) * 10.0) / 10.0;
            int processingTime = 4 + rand.nextInt(68);

            // Compute delay probability
            double delayProbability = (distance / 2500.0) * 0.3 + (1.0 - supplierScore) * 0.4 + (processingTime / 72.0) * 0.3;
            int isDelayed = rand.nextDouble() < delayProbability ? 1 : 0;

            supplierScoreCol.append(supplierScore);
            inventoryCol.append(inventory);
            distanceCol.append(distance);
            processingTimeCol.append(processingTime);
            isDelayedCol.append(isDelayed);
        }

        Table supplyChainTable = Table.create("RawSupplyChainData",
                orderIdCol, carrierCol, warehouseCol, shippingModeCol,
                supplierScoreCol, inventoryCol, distanceCol, processingTimeCol, isDelayedCol);

        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdir();
        }

        String outputPath = "data/raw_supply_chain_java.csv";
        supplyChainTable.write().csv(outputPath);
        System.out.println("Dataset generated successfully [" + totalRecords + " records] -> " + outputPath);
        return outputPath;
    }
}