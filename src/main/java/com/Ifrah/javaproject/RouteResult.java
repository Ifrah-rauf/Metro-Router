package com.Ifrah.javaproject;

import java.util.List;

public class RouteResult {
    public double shortestD;
    public List<String> shortestP;

    public RouteResult(double shortestDistance, List<String> shortestPathNodes) {
        this.shortestD = shortestDistance;
        this.shortestP = shortestPathNodes;
    }
}
