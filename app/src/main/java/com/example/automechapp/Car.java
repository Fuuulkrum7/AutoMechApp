package com.example.automechapp;

public class Car {
    private String carName;
    private String manufacture;
    private String model;
    private int id;

    public Car(String carName, String manufacture, String model, int id) {
        this.carName = carName;
        this.manufacture = manufacture;
        this.model = model;
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getManufacture() {
        return manufacture;
    }

    public void setManufacture(String manufacture) {
        this.manufacture = manufacture;
    }

    public String getCarName() {
        return carName;
    }

    public void setCar_name(String carName) {
        this.carName = carName;
    }
}
