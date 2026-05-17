package com.Ifrah.javaproject;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class TransitWeightService {

    /*
     * Weekday multipliers.
     *
     * These simulate how crowded the metro becomes depending on the day.
     *
     * Example:
     * Monday office rush > Sunday leisure traffic
     */
    private final Map<DayOfWeek, Double> weekdayFactors = new HashMap<>();

    /*
     * Waiting frequency per line (in minutes).
     *
     * Example:
     * Yellow line train every 2 mins during peak.
     *
     * Later this can become:
     * peakFrequency + normalFrequency separately.
     */
    private final Map<String, Integer> lineWaitingTimes = new HashMap<>();

//     * Interchange walking penalties.
    private final Map<String, Integer> interchangePenalties = new HashMap<>();


    public TransitWeightService() {
//         * --------------------------
//         * WEEKDAY TRAFFIC FACTORS
//         * --------------------------

        weekdayFactors.put(DayOfWeek.MONDAY, 1.25);
        weekdayFactors.put(DayOfWeek.TUESDAY, 1.20);
        weekdayFactors.put(DayOfWeek.WEDNESDAY, 1.20);
        weekdayFactors.put(DayOfWeek.THURSDAY, 1.20);
        weekdayFactors.put(DayOfWeek.FRIDAY, 1.25);

        weekdayFactors.put(DayOfWeek.SATURDAY, 0.50);
        weekdayFactors.put(DayOfWeek.SUNDAY, 0.25);

//         * --------------------------
//         * AVERAGE WAITING TIMES
//         * --------------------------
        lineWaitingTimes.put("Yellow Line", 2);
        lineWaitingTimes.put("Blue Line", 3);

//         * --------------------------
//         * INTERCHANGE PENALTIES
//         * --------------------------
        interchangePenalties.put("Rajiv Chowk", 1);
        interchangePenalties.put("Kashmere Gate", 2);
    }


    /*
     * Main runtime weight calculation.
     *
     * This method is called DURING Dijkstra traversal.
     *
     * Inputs:
     * -------
     * edge:
     *     current edge being explored
     *
     * currentTime:
     *     actual traversal time at current node
     *
     * currentStation:
     *     current station where traversal is happening
     *
     *
     * Output:
     * -------
     * Final dynamic traversal cost for this edge.
     */
    public double calculateWeight(
            Edge edge,
            LocalDateTime currentTime,
            String currentStation,
            boolean applyInterchangePenalty
    ) {

        double baseTravelTime = edge.getBaseTravelTime();

//         * --------------------------
//         * WAITING TIME
//         * --------------------------
        double waitingTime = getWaitingTime(edge.getLine());

//         * --------------------------
//         * INTERCHANGE WALKING PENALTY
//         * --------------------------
        double interchangePenalty = applyInterchangePenalty ? getInterchangePenalty(currentStation) : 0;

//         * --------------------------
//         * WEEKDAY TRAFFIC FACTOR
//         * --------------------------
        double weekdayFactor = getWeekdayFactor(currentTime.getDayOfWeek());

//         * --------------------------
//         * PEAK HOUR MULTIPLIER
//         * --------------------------
        double peakHourMultiplier =
                getPeakHourMultiplier(currentTime);

//         * --------------------------
//         * CROWD MULTIPLIER
//         * --------------------------
        double crowdMultiplier = weekdayFactor * peakHourMultiplier;

//         * --------------------------
//         * FINAL DYNAMIC EDGE COST
//         * --------------------------
        return (
                baseTravelTime
                        + waitingTime
                        + interchangePenalty
                        + crowdMultiplier);
    }



    /*
     * Returns average waiting time for a metro line.
     */
    private double getWaitingTime(String line) {

        return lineWaitingTimes.getOrDefault(line, 3);
    }



    /*
     * Returns interchange walking penalty.
     *
     * Non-interchange stations return 0.
     */
    private double getInterchangePenalty(String station) {

        return interchangePenalties.getOrDefault(station, 0);
    }



    /*
     * Returns weekday crowd multiplier.
     */
    private double getWeekdayFactor(DayOfWeek day) {

        return weekdayFactors.getOrDefault(day, 1.0);
    }



    /*
     * Peak hour detection.
     *
     * Morning:
     * 7 AM - 10 AM
     *
     * Evening:
     * 5 PM - 8 PM
     */
    private double getPeakHourMultiplier(LocalDateTime currentDateTime) {

        DayOfWeek day = currentDateTime.getDayOfWeek();
        LocalTime time = currentDateTime.toLocalTime();

        // Weekend traffic remains relaxed.
        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            return 1.0;
        }

        // Office rush timings for weekdays.
        LocalTime morningStart = LocalTime.of(8, 0);
        LocalTime morningEnd = LocalTime.of(10, 0);

        LocalTime eveningStart = LocalTime.of(17, 0);
        LocalTime eveningEnd = LocalTime.of(19, 0);

        boolean morningPeak =
                !time.isBefore(morningStart)
                        && !time.isAfter(morningEnd);

        boolean eveningPeak =
                !time.isBefore(eveningStart)
                        && !time.isAfter(eveningEnd);

        // Heavy weekday office crowd multiplier.
        if (morningPeak || eveningPeak) {
            return 1.6;
        }

        // Normal weekday traffic.
        return 1.1;
    }
}
