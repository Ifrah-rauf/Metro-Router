package com.Ifrah.javaproject;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "Blue")
public class blue {
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
    @Field("branchCode")
    private String branchCode;
    @Field("branchTop")
    private boolean branchTop;


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

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public boolean isBranchTop() {
        return branchTop;
    }

    public void setBranchTop(boolean branchTop) {
        this.branchTop = branchTop;
    }
}
