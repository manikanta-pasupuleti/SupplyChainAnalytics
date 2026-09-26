package com.supplychain;

import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class DatabaseExporter {

    private DatabaseExporter() {
    }

    public static void persistIfConfigured(String csvPath) throws Exception {
        String databaseUrl = System.getenv("SUPPLY_CHAIN_DB_URL");
        if (databaseUrl == null || databaseUrl.isBlank()) {
            System.out.println("Database export skipped: SUPPLY_CHAIN_DB_URL is not configured.");
            return;
        }

        String username = System.getenv().getOrDefault("SUPPLY_CHAIN_DB_USER", "postgres");
        String password = System.getenv().getOrDefault("SUPPLY_CHAIN_DB_PASSWORD", "");
        Table table = Table.read().csv(csvPath);
        String insertSql = "INSERT INTO shipments (order_id, carrier_id, warehouse_id, shipping_mode, "
                + "supplier_reliability_score, warehouse_inventory_level, shipping_distance_km, "
                + "processing_time_hours, is_delayed) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) "
                + "ON CONFLICT (order_id) DO UPDATE SET is_delayed = EXCLUDED.is_delayed";

        try (Connection connection = DriverManager.getConnection(databaseUrl, username, password)) {
            createTableIfNeeded(connection);
            try (PreparedStatement statement = connection.prepareStatement(insertSql)) {
                StringColumn orderIds = table.stringColumn("order_id");
                StringColumn carriers = table.stringColumn("carrier_id");
                StringColumn warehouses = table.stringColumn("warehouse_id");
                StringColumn shippingModes = table.stringColumn("shipping_mode");
                DoubleColumn supplierScores = table.doubleColumn("supplier_reliability_score");
                IntColumn inventory = table.intColumn("warehouse_inventory_level");
                DoubleColumn distances = table.doubleColumn("shipping_distance_km");
                IntColumn processingTimes = table.intColumn("processing_time_hours");
                IntColumn delayed = table.intColumn("is_delayed");

                for (int row = 0; row < table.rowCount(); row++) {
                    statement.setString(1, orderIds.get(row));
                    statement.setString(2, carriers.get(row));
                    statement.setString(3, warehouses.get(row));
                    statement.setString(4, shippingModes.get(row));
                    statement.setDouble(5, supplierScores.get(row));
                    statement.setInt(6, inventory.get(row));
                    statement.setDouble(7, distances.get(row));
                    statement.setInt(8, processingTimes.get(row));
                    statement.setInt(9, delayed.get(row));
                    statement.addBatch();
                }
                statement.executeBatch();
            }
        }
        System.out.println("Database export completed: " + table.rowCount() + " shipments persisted.");
    }

    private static void createTableIfNeeded(Connection connection) throws SQLException {
        String ddl = "CREATE TABLE IF NOT EXISTS shipments ("
                + "order_id VARCHAR(32) PRIMARY KEY, carrier_id VARCHAR(64) NOT NULL, "
                + "warehouse_id VARCHAR(128) NOT NULL, shipping_mode VARCHAR(64) NOT NULL, "
                + "supplier_reliability_score DOUBLE PRECISION NOT NULL, "
                + "warehouse_inventory_level INTEGER NOT NULL, shipping_distance_km DOUBLE PRECISION NOT NULL, "
                + "processing_time_hours INTEGER NOT NULL, is_delayed INTEGER NOT NULL)";
        try (PreparedStatement statement = connection.prepareStatement(ddl)) {
            statement.execute();
        }
    }
}
