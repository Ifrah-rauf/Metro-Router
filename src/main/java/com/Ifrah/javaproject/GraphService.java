package com.Ifrah.javaproject;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class GraphService {
    private ArrayList<Edge>[] cachedGraph;
    private boolean graphInitialized;

    public synchronized ArrayList<Edge>[] buildGraph(List<yellow> yellowStations, List<blue> blueStations) {
        if (graphInitialized && cachedGraph != null) {
            System.out.println("cached graph");
            return cachedGraph;
        }
        else{
            System.out.println("built graph");
        }

        // Keep both lines in a stable code order before building adjacency lists.
        List<yellow> sortedYellow = new ArrayList<>(yellowStations);
        sortedYellow.sort(Comparator.comparingInt(station -> extractCodeNumber(station.getCode())));

        List<blue> sortedBlue = new ArrayList<>(blueStations);
        sortedBlue.sort(Comparator.comparingInt(station -> extractCodeNumber(station.getCode())));

        // One adjacency bucket per station node.
        ArrayList<Edge>[] graph = new ArrayList[sortedYellow.size() + sortedBlue.size()];

        for (int i = 0; i < graph.length; i++) {
            graph[i] = new ArrayList<>();
        }

        // Yellow line remains a simple linear chain: connect each station to the next.
        for (int i = 0, j = i + 1; i < sortedYellow.size() - 1 && j < sortedYellow.size(); i++, j++) {
            yellow srcNode = sortedYellow.get(i);
            yellow destNode = sortedYellow.get(j);
            double dist = cal(srcNode.getLatitude(), srcNode.getLongitude(), destNode.getLatitude(), destNode.getLongitude());
            // - distance_persisted: store the computed distance directly on the directed edge.
            Edge forward = new Edge(srcNode.getStation(), destNode.getStation(), dist, "Yellow Line");
            Edge reverse = new Edge(destNode.getStation(), srcNode.getStation(), dist, "Yellow Line");
            forward.setInterchangeAware(false);
            reverse.setInterchangeAware(false);
            graph[i].add(forward);
            graph[j].add(reverse);
        }

        // Blue stations start after yellow stations in the combined adjacency array.
        int startIdx = sortedYellow.size();
        Map<String, Integer> blueIndexByCode = new HashMap<>();
        for (int i = 0; i < sortedBlue.size(); i++) {
            blueIndexByCode.put(sortedBlue.get(i).getCode(), startIdx + i);
        }

        // Find the station that marks the split point of the blue network.
        int branchTopIndex = -1;
        for (int i = 0; i < sortedBlue.size(); i++) {
            if (sortedBlue.get(i).isBranchTop()) {
                branchTopIndex = i;
                break;
            }
        }

        // If no branch point exists, fall back to the older simple linear blue chain.
        if (branchTopIndex == -1) {
            for (int i = 0, j = i + 1; i < sortedBlue.size() - 1 && j < sortedBlue.size(); i++, j++) {
                blue srcNode = sortedBlue.get(i);
                blue destNode = sortedBlue.get(j);
                double dist = cal(srcNode.getLatitude(), srcNode.getLongitude(), destNode.getLatitude(), destNode.getLongitude());
                // - distance_persisted: store the computed distance directly on the blue edge object.
                Edge forward = new Edge(srcNode.getStation(), destNode.getStation(), dist, "Blue Line");
                Edge reverse = new Edge(destNode.getStation(), srcNode.getStation(), dist, "Blue Line");
                forward.setInterchangeAware("Rajiv Chowk".equalsIgnoreCase(srcNode.getStation()));
                reverse.setInterchangeAware("Rajiv Chowk".equalsIgnoreCase(destNode.getStation()));
                graph[startIdx + i].add(forward);
                graph[startIdx + j].add(reverse);
            }
            cachedGraph = graph;
            graphInitialized = true;
            return graph;
        }

        // Connecting the blue stations that come before the branch point as a single trunk.
        for (int i = 0; i < branchTopIndex; i++) {
            blue srcNode = sortedBlue.get(i);
            blue destNode = sortedBlue.get(i + 1);
            double dist = cal(srcNode.getLatitude(), srcNode.getLongitude(), destNode.getLatitude(), destNode.getLongitude());
            // - distance_persisted: keep the shared trunk distance on the edge object only.
            Edge forward = new Edge(srcNode.getStation(), destNode.getStation(), dist, "Blue Line");
            Edge reverse = new Edge(destNode.getStation(), srcNode.getStation(), dist, "Blue Line");
            forward.setInterchangeAware("Rajiv Chowk".equalsIgnoreCase(srcNode.getStation()));
            reverse.setInterchangeAware("Rajiv Chowk".equalsIgnoreCase(destNode.getStation()));
            graph[startIdx + i].add(forward);
            graph[startIdx + i + 1].add(reverse);
        }

        // Grouping the remaining blue stations by branchCode so each branch is built independently.
        Map<String, List<blue>> branchGroups = new LinkedHashMap<>();
        for (blue station : sortedBlue) {
            if (!station.isBranchTop() && station.getBranchCode() != null && !station.getBranchCode().isBlank()) {
                branchGroups.computeIfAbsent(station.getBranchCode(), key -> new ArrayList<>()).add(station);
            }
        }

        // At this point the branch point itself is known, but the branch legs are still separate.
        // We first connect the branch top to the first station in each branch.
        // After that, each branch is stitched together only within its own branchCode group.
        // This keeps the Vaishali side and the Noida side independent instead of merging them into one chain.
        blue branchTop = sortedBlue.get(branchTopIndex);
        int branchTopGraphIndex = startIdx + branchTopIndex;
        for (List<blue> branchStations : branchGroups.values()) {
            if (branchStations.isEmpty()) {
                continue;
            }

            // Branch stations are already tagged with branchCode in the database.
            // Sorting inside the group preserves the local order for that branch only.
            branchStations.sort(Comparator.comparingInt(station -> extractCodeNumber(station.getCode())));

            // The first station of a branch is the direct child of the branch point.
            // Example: Yamuna Bank -> Laxmi Nagar for one branch, Yamuna Bank -> Akshardham for another.
            blue firstBranchStation = branchStations.get(0);
            Integer firstBranchIndex = blueIndexByCode.get(firstBranchStation.getCode());
            if (firstBranchIndex != null) {
                double dist = cal(branchTop.getLatitude(), branchTop.getLongitude(),
                        firstBranchStation.getLatitude(), firstBranchStation.getLongitude());
                // - distance_persisted: connect the branch point to the first station of this branch once.
                Edge forward = new Edge(branchTop.getStation(), firstBranchStation.getStation(), dist, "Blue Line");
                Edge reverse = new Edge(firstBranchStation.getStation(), branchTop.getStation(), dist, "Blue Line");
                forward.setInterchangeAware("Rajiv Chowk".equalsIgnoreCase(branchTop.getStation()));
                reverse.setInterchangeAware("Rajiv Chowk".equalsIgnoreCase(firstBranchStation.getStation()));
                graph[branchTopGraphIndex].add(forward);
                graph[firstBranchIndex].add(reverse);
            }

            // Once the branch starts, link each station to the next station in the same branch only.
            // This is what prevents the path from jumping across to the parallel branch.
            for (int i = 0; i < branchStations.size() - 1; i++) {
                blue srcNode = branchStations.get(i);
                blue destNode = branchStations.get(i + 1);
                Integer srcIndex = blueIndexByCode.get(srcNode.getCode());
                Integer destIndex = blueIndexByCode.get(destNode.getCode());
                if (srcIndex == null || destIndex == null) {
                    continue;
                }

                double dist = cal(srcNode.getLatitude(), srcNode.getLongitude(),
                        destNode.getLatitude(), destNode.getLongitude());
                // - distance_persisted: every intra-branch edge keeps its one-time calculated distance.
                Edge forward = new Edge(srcNode.getStation(), destNode.getStation(), dist, "Blue Line");
                Edge reverse = new Edge(destNode.getStation(), srcNode.getStation(), dist, "Blue Line");
                forward.setInterchangeAware("Rajiv Chowk".equalsIgnoreCase(srcNode.getStation()));
                reverse.setInterchangeAware("Rajiv Chowk".equalsIgnoreCase(destNode.getStation()));
                graph[srcIndex].add(forward);
                graph[destIndex].add(reverse);
            }
        }

        cachedGraph = graph;
        graphInitialized = true;
        return graph;
    }

    public static int extractCodeNumber(String code) {
        if (code == null) {
            return -1;
        }
        String digits = code.replaceAll("\\D+", "");
        if (digits.isEmpty()) {
            return -1;
        }
        return Integer.parseInt(digits);
    }

    private double cal(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371;
        double lat1Rad = toRadians(lat1);
        double lon1Rad = toRadians(lon1);
        double lat2Rad = toRadians(lat2);
        double lon2Rad = toRadians(lon2);

        double dLat = lat2Rad - lat1Rad;
        double dLon = lon2Rad - lon1Rad;

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private double toRadians(double degree) {
        return degree * Math.PI / 180.0;
    }
}
