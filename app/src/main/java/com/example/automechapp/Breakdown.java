package com.example.automechapp;

import java.util.Date;

public class Breakdown {
    private String breakdown_name;
    private String manufacture;
    private String model;
    private String date;
    private String time;
    private int id;

    public Breakdown
            (String breakdown_name, String manufacture, String model, String date, String time, int id) {
        this.breakdown_name = breakdown_name;
        this.manufacture = manufacture;
        this.model = model;
        this.date = date;
        this.time = time;
        this.id = id;
    }

    public String getBreakdown_name() {
        return breakdown_name;
    }

    public void setBreakdown_name(String breakdown_name) {
        this.breakdown_name = breakdown_name;
    }

    public String getManufacture() {
        return manufacture;
    }

    public void setManufacture(String manufacture) {
        this.manufacture = manufacture;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
