package com.Ifrah.javaproject;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "Yellow")
public class yellow {
    @Id
    @Field("_id")
    private String ID;
    @Field("Station")
    private String station;
    @Field("Line")
    private String line;
    @Field("Latitude")
    private double latitude;
    @Field("Longitude")
    private double longitude;
    @Field("Code")
    private String code;
    @Field("Distance")
    private double distance;


    // Getters and Setters

    public void setID(String ID) {
        this.ID = ID;
    }
    public String getID() {
        return ID;
    }
    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }
}
