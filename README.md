# Java Supply Chain Analytics Capstone

An end-to-end Java 17 and Maven analytics platform for identifying carrier performance problems, warehouse bottlenecks, and shipment delay risk.

## Capstone capabilities

- Generates 3,000 deterministic synthetic shipment records.
- Loads and aggregates CSV data with Tablesaw 0.43.1.
- Calculates carrier delay rates and On-Time Delivery (OTD %).
- Ranks warehouse processing bottlenecks.
- Computes a bounded heuristic risk score and high-risk tier.
- Produces CSV reports and an HTML dashboard.
- Supports optional PostgreSQL persistence through JDBC.
- Includes JUnit tests for the prediction engine.
- Provides a Spring Boot REST API for dashboards and integrations.

## Run the pipeline

```powershell
mvn clean compile
mvn test
mvn exec:java '-Dexec.mainClass=com.supplychain.App'
```

## Run the REST API

Generate the reports first, then start the API:

```powershell
mvn exec:java '-Dexec.mainClass=com.supplychain.App'
mvn spring-boot:run -Dspring-boot.run.main-class=com.supplychain.SupplyChainApiApplication
```

Available endpoints:

- `GET /api/health`
- `GET /api/carriers`
- `GET /api/warehouses/bottlenecks`
- `GET /api/shipments/high-risk`
- `GET /api/shipments/{order_id}`

Generated files are written to `data/`:

- `raw_supply_chain_java.csv`
- `carrier_summary.csv`
- `warehouse_bottlenecks.csv`
- `supply_chain_predictions.csv`
- `dashboard.html`

Open `data/dashboard.html` in a browser to review the carrier and warehouse summaries.

## Optional PostgreSQL persistence

The pipeline remains CSV-first by default. To persist raw shipments, configure these environment variables before running:

```powershell
$env:SUPPLY_CHAIN_DB_URL = "jdbc:postgresql://localhost:5432/supplychain"
$env:SUPPLY_CHAIN_DB_USER = "postgres"
$env:SUPPLY_CHAIN_DB_PASSWORD = "your-password"
mvn exec:java '-Dexec.mainClass=com.supplychain.App'
```

The exporter creates a `shipments` table if it does not exist and uses `order_id` as the primary key.

## Architecture

`App` orchestrates the pipeline. `DataGenerator` creates operational records, `DataAnalyzer` computes KPIs, `DelayPredictor` scores risk, `DatabaseExporter` provides optional JDBC persistence, and `DashboardGenerator` creates the report artifact.
