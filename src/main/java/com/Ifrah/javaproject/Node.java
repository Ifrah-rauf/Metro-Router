package com.Ifrah.javaproject;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Node {
    String stationName;
    String line;
    boolean isInterchange;
    String code;

    public Node(String stationName, String line, boolean isInterchange, String code) {
        this.stationName = stationName;
        this.line = line;
        this.isInterchange = isInterchange;
        this.code = code;
    }

    public String getLine() {
        return line;
    }

    public String getSt() {
        return stationName;
    }

    public String getCode() {
        return code;
    }

    public int getCodeNumber() {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(code);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group());
        }
        return -1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return stationName.equals(node.stationName) && line.equals(node.line) && code.equals(node.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stationName, line, code);
    }
}
