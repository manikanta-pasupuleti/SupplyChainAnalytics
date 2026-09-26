package com.supplychain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DelayPredictorTest {

    @Test
    void riskScoreIncreasesForLongerDistance() {
        double shortRoute = DelayPredictor.calculateRiskScore(100, 24, 0.9);
        double longRoute = DelayPredictor.calculateRiskScore(2400, 24, 0.9);

        assertTrue(longRoute > shortRoute);
    }

    @Test
    void riskScoreIsClampedToTheDocumentedRange() {
        assertEquals(0.0, DelayPredictor.calculateRiskScore(-100, -10, 1.5));
        assertEquals(1.0, DelayPredictor.calculateRiskScore(10000, 1000, -1.0));
    }

    @Test
    void highRiskThresholdMatchesPipelineRule() {
        double score = DelayPredictor.calculateRiskScore(2000, 60, 0.55);
        assertTrue(score > 0.50);
    }
}
