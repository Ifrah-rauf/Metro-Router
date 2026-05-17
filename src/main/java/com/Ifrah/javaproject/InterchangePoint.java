package com.Ifrah.javaproject;

import java.util.ArrayList;
import java.util.List;

public class InterchangePoint {
    private String station;
    private List<String> lines;

    public InterchangePoint(String station, List<String> lines) {
        this.station = station;
        this.lines = new ArrayList<>(lines);
    }

    public String getStation() {
        return station;
    }

    public List<String> getLines() {
        return lines;
    }

    public void addLine(String line) {
        if (!lines.contains(line)) {
            lines.add(line);
        }
    }
}
