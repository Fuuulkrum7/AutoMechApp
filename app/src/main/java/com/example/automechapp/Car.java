package com.example.automechapp;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class Car {
    private String carName;
    private String manufacture;
    private String model;
    private int id;
    private Bitmap icon;
    private int user_id;

    private int car_year;
    private int car_price;
    private int tax;
    private String car_color;
    private String VIN;
    private String car_state_number;
    private int engine_volume;
    private String engine_number;
    private String engine_model;
    private ArrayList<Bitmap> car_photos;
    private int horsepower;

    public static int defaultCar = 6;

    public Car(String carName, String manufacture, String model, int id, Bitmap icon, int user_id) {
        this.carName = carName;
        this.manufacture = manufacture;
        this.model = model;
        this.id = id;
        this.icon = icon;
        this.user_id = user_id;
    }

    public Car(String carName, String manufacture, String model, int id, Bitmap icon, int user_id, int car_year, int car_price, int tax, String car_color, String vin, String car_state_number, int engine_volume, String engine_number, String engine_model, int horsepower, ArrayList<Bitmap> car_photos) {
        this.carName = carName;
        this.manufacture = manufacture;
        this.model = model;
        this.id = id;
        this.icon = icon;
        this.user_id = user_id;
        this.car_year = car_year;
        this.car_price = car_price;
        this.tax = tax;
        this.car_color = car_color;
        VIN = vin;
        this.car_state_number = car_state_number;
        this.engine_volume = engine_volume;
        this.engine_number = engine_number;
        this.engine_model = engine_model;
        this.car_photos = car_photos;
        this.horsepower = horsepower;
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

    public String getCar_state_number() {
        return car_state_number;
    }

    public void setCar_state_number(String car_state_number) {
        this.car_state_number = car_state_number;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getCar_year() {
        return car_year;
    }

    public void setCar_year(int car_year) {
        this.car_year = car_year;
    }

    public int getCar_price() {
        return car_price;
    }

    public void setCar_price(int car_price) {
        this.car_price = car_price;
    }

    public int getTax() {
        return tax;
    }

    public void setTax(int tax) {
        this.tax = tax;
    }

    public String getCar_color() {
        return car_color;
    }

    public void setCar_color(String car_color) {
        this.car_color = car_color;
    }

    public String getVIN() {
        return VIN;
    }

    public void setVIN(String VIN) {
        this.VIN = VIN;
    }

    public int getEngine_volume() {
        return engine_volume;
    }

    public void setEngine_volume(int engine_volume) {
        this.engine_volume = engine_volume;
    }

    public String getEngine_number() {
        return engine_number;
    }

    public void setEngine_number(String engine_number) {
        this.engine_number = engine_number;
    }

    public String getEngine_model() {
        return engine_model;
    }

    public void setEngine_model(String engine_model) {
        this.engine_model = engine_model;
    }

    public ArrayList<Bitmap> getCar_photos() {
        return car_photos;
    }

    public void setCar_photos(ArrayList<Bitmap> car_photos) {
        this.car_photos = car_photos;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public int getHorsepower() {
        return horsepower;
    }

    public void setHorsepower(int horsepower) {
        this.horsepower = horsepower;
    }
}
