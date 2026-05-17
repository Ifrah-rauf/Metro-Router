package com.Ifrah.javaproject;

import java.time.LocalDateTime;

public class TraversalTimeManager {

    // Journey start timestamp.
    private LocalDateTime routeStartTime;

    // Total accumulated traversal minutes.
    private double accumulatedTravelMinutes;

    // Initialize traversal clock.
    public TraversalTimeManager() {
        reset();
    }

    // Add edge travel time.
    public void addTraversalMinutes(double minutes) {
        accumulatedTravelMinutes += minutes;
    }

    // Return simulated current traversal time.
    public LocalDateTime getCurrentTraversalTime() {
        return routeStartTime.plusMinutes((long) accumulatedTravelMinutes);
    }

    // Return total accumulated travel time.
    public double getAccumulatedTravelMinutes() {
        return accumulatedTravelMinutes;
    }

    // Reset traversal state.
    public void reset() {
        routeStartTime = LocalDateTime.now();
        accumulatedTravelMinutes = 0;
    }
}
