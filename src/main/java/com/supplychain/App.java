package com.supplychain;

public class App {
    public static void main(String[] args) {
        try {
            System.out.println("==================================================");
            System.out.println("   STARTING JAVA SUPPLY CHAIN ANALYTICS PIPELINE  ");
            System.out.println("==================================================\n");

            // Step 1: Generate Raw Supply Chain Data (3,000 records)
            String dataPath = DataGenerator.generateSupplyChainData(3000);

            // Step 2: Compute Summary Aggregations
            DataAnalyzer.processAndAnalyze(dataPath);

            // Step 3: Run Delay Prediction ML Pipeline
            DelayPredictor.trainAndPredict(dataPath);

            System.out.println("\n[SUCCESS] End-To-End Java Pipeline Executed Successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}