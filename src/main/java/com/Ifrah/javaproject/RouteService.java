package com.Ifrah.javaproject;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class RouteService {
    TraversalTimeManager traversalClock =  new TraversalTimeManager();
    private final TransitWeightService transitWeightService;

    public RouteService(TransitWeightService transitWeightService) {
        this.transitWeightService = transitWeightService;
    }
    private final List<InterchangePoint> interchangePoints = new ArrayList<>();

    public String check(List<Node> nodes, List<blue> b, List<yellow> y, String src, String dest) {
        // This is not a graph edge. It is the interchange decision point used by the path logic.
        // Rajiv Chowk is treated as the shared transfer station between yellow and blue routes.
        interchangePoints.clear();
        List<String> rajivLines = new ArrayList<>();
        rajivLines.add("Yellow Line");
        rajivLines.add("Blue Line");
        interchangePoints.add(new InterchangePoint("Rajiv Chowk", rajivLines));

        String destLine = "", srcLine = "";
        for (Node n : nodes) {
            if (n.getSt().equals(src)) {
                srcLine = n.line;
            }
            if (n.getSt().equals(dest)) {
                destLine = n.line;
            }
        }

        List<String> comp = new ArrayList<>();
        comp.add(srcLine);
        comp.add(destLine);
        if (!srcLine.equals(destLine)) {
            for (InterchangePoint interchange : interchangePoints) {
                if (interchange.getLines().containsAll(comp)) {
                    return interchange.getStation();
                }
            }
        }
        return dest;
    }

    public RouteResult bellman2(ArrayList<Edge> graph2[], List<blue> b, List<yellow> y, int V, String src, String destnode) {
        traversalClock.reset();
        ArrayList<Node> nodes = new ArrayList<>();

        for (yellow station : y) {
            nodes.add(new Node(station.getStation(), "Yellow Line", false, station.getCode()));
        }

        for (blue station : b) {
            nodes.add(new Node(station.getStation(), "Blue Line", false, station.getCode()));
        }

        for (Node node : nodes) {
            if (node.stationName.equals("Rajiv Chowk")) {
                node.isInterchange = true;
            }
        }

        List<Node> yellowNodes = nodes.stream()
                .filter(n -> n.getLine().equalsIgnoreCase("Yellow Line"))
                .sorted(Comparator.comparing(Node::getCodeNumber))
                .collect(Collectors.toList());

        List<Node> blueNodes = nodes.stream()
                .filter(n -> n.getLine().equalsIgnoreCase("Blue Line"))
                .sorted(Comparator.comparing(Node::getCodeNumber))
                .collect(Collectors.toList());

        nodes.clear();
        nodes.addAll(yellowNodes);
        nodes.addAll(blueNodes);

        String line = "";
        String sourceLine = "";
        String destLine = "";
        String interchange = check(nodes, b, y, src, destnode);
        for (Node node : nodes) {
            if (node.getSt().equals(src)) {
                line = node.getLine();
                sourceLine = node.getLine();
            }
            if (node.getSt().equals(destnode)) {
                destLine = node.getLine();
            }
        }

        RouteResult srcToInterchange = bellman(graph2, nodes, b, y, V, src, interchange, line, false);

        boolean applyInterchangePenalty = !sourceLine.equalsIgnoreCase(destLine);
        RouteResult interchangeToDest = bellman(graph2, nodes, b, y, V, interchange, destnode, destLine, applyInterchangePenalty);

        double totalDistance = srcToInterchange.shortestD + interchangeToDest.shortestD;
        List<String> answer = new ArrayList<>(srcToInterchange.shortestP);
        List<String> tail = new ArrayList<>(interchangeToDest.shortestP);
        if (!answer.isEmpty() && !tail.isEmpty() && answer.get(answer.size() - 1).equalsIgnoreCase(tail.get(0))) {
            tail.remove(0);
        }
        answer.addAll(tail);

        return new RouteResult(totalDistance, answer);
    }

    public RouteResult bellman(ArrayList<Edge> graph2[], List<Node> nodes, List<blue> b, List<yellow> y, int V, String src, String destnode, String line, boolean allowInterchangePenalty) {
        List<Integer> filteredIndices = IntStream.range(0, nodes.size())
                .filter(i -> nodes.get(i).line.equalsIgnoreCase(line))
                .boxed()
                .collect(Collectors.toList());

        List<Node> filteredNodes = nodes.stream()
                .filter(node -> node.line.equalsIgnoreCase(line))
                .sorted(Comparator.comparingInt(node -> node.getCodeNumber()))
                .collect(Collectors.toList());

        Map<String, Integer> localIndexByStation = new HashMap<>();
        for (int i = 0; i < filteredNodes.size(); i++) {
            localIndexByStation.put(filteredNodes.get(i).stationName.toLowerCase(), i);
        }

        Integer sourceIndex = localIndexByStation.get(src.toLowerCase());
        Integer destIndex = localIndexByStation.get(destnode.toLowerCase());
        if (sourceIndex == null || destIndex == null) {
            return new RouteResult(Double.MAX_VALUE, new ArrayList<>());
        }

        double[] dist = new double[filteredNodes.size()];
        int[] predecessor = new int[filteredNodes.size()];
        boolean[] visited = new boolean[filteredNodes.size()];
        for (int i = 0; i < filteredNodes.size(); i++) {
            dist[i] = Double.MAX_VALUE;
            predecessor[i] = -1;
        }
        dist[sourceIndex] = 0.0;

        PriorityQueue<NodeDistance> queue = new PriorityQueue<>(Comparator.comparingDouble(nd -> nd.distance));
        queue.add(new NodeDistance(sourceIndex, 0.0));

        while (!queue.isEmpty()) {
            NodeDistance current = queue.poll();
            int u = current.index;
            if (visited[u]) {
                continue;
            }
            visited[u] = true;

            int graphIndex = filteredIndices.get(u);
            if (graphIndex < 0 || graphIndex >= graph2.length) {
                continue;
            }

            for (Edge edge : graph2[graphIndex]) {

                Integer v = localIndexByStation.get(edge.dest.toLowerCase());

                if (v == null) {
                    continue;
                }

                // Dynamic routing based on traversal time, weekday traffic and congestion.
                traversalClock.addTraversalMinutes(edge.getBaseTravelTime());

                double edgeWeight =
                        transitWeightService.calculateWeight(
                                edge,
                                traversalClock.getCurrentTraversalTime(),
                                filteredNodes.get(u).stationName,
                                allowInterchangePenalty && edge.isInterchangeAware()
                        );

                double newDistance = dist[u] + edgeWeight;

                if (newDistance < dist[v]) {

                    dist[v] = newDistance;

                    predecessor[v] = u;

                    queue.add(new NodeDistance(v, newDistance));
                }
            }
        }

        List<String> path = new ArrayList<>();
        if (dist[destIndex] == Double.MAX_VALUE) {
            return new RouteResult(Double.MAX_VALUE, path);
        }

        for (int at = destIndex; at != -1; at = predecessor[at]) {
            path.add(filteredNodes.get(at).stationName);
        }
        Collections.reverse(path);

        return new RouteResult(dist[destIndex], path);
    }

    private static class NodeDistance {
        private final int index;
        private final double distance;

        private NodeDistance(int index, double distance) {
            this.index = index;
            this.distance = distance;
        }
    }
}
