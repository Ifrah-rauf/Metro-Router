package com.Ifrah.javaproject;

public class Edge {
    String src;
    String dest;
    double distanceKm;
    double baseTravelTime;
    String line;
    boolean interchangeAware;
    public Edge(String src, String dest, double distanceKm,String line) {
        this.src = src;
        this.dest = dest;
        this.distanceKm = distanceKm;
        this.baseTravelTime = 4.0;
        this.line=line;
        this.interchangeAware = false;
    }

    public String getSrc() {
        return src;
    }

    public String getDst() {
        return dest;
    }

    public double getDistanceKm() {
//        System.out.println("in distance");
        return distanceKm;
    }

    public double getBaseTravelTime() {
        return baseTravelTime;
    }

    public String getLine() {
        return line;
    }

    public boolean isInterchangeAware() {
        return interchangeAware;
    }

    public void setInterchangeAware(boolean interchangeAware) {
        this.interchangeAware = interchangeAware;
    }
}
