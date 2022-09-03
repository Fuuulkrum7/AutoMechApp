package com.example.automechapp;

// Класс для полломки (в т.ч. как для адаптера, так и для активности с поломкой
public class Breakdown {
    // Имя поломки, фирма и модель машины, дата поломки, время добавления
    private String breakdown_name;
    private String manufacture;
    private String model;
    private String date;
    private String time;
    // id поломки в бд и id авто
    private int id;
    private int car_id;

    // Конструктор. Далее - сеттеры и геттеры
    public Breakdown
            (String breakdown_name, String manufacture, String model, String date, String time, int id, int car_id) {
        this.breakdown_name = breakdown_name;
        this.manufacture = manufacture;
        this.model = model;
        this.date = date;
        this.time = time;
        this.id = id;
        this.car_id = car_id;
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

    public int getCar_id() {
        return car_id;
    }

    public void setCar_id(int car_id) {
        this.car_id = car_id;
    }
}
