package com.example.automechapp;

import android.graphics.Bitmap;

import java.util.ArrayList;

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
    //
    private int work_price;
    private String comment;
    private String description;
    private int type;
    private Bitmap icon;
    private ArrayList<Bitmap> photos;

    // Конструктор. Далее - сеттеры и геттеры
    public Breakdown
            (String breakdown_name, String manufacture, String model, Bitmap icon, String date, String time, int id, int car_id) {
        this.breakdown_name = breakdown_name;
        this.manufacture = manufacture;
        this.model = model;
        this.icon = icon;
        this.date = date;
        this.time = time;
        this.id = id;
        this.car_id = car_id;
    }

    //
    public Breakdown(String breakdown_name, Bitmap icon, String date, String time, int id, int car_id, int work_price, String comment, String description, int type, ArrayList<Bitmap> photos) {
        this.breakdown_name = breakdown_name;
        this.icon = icon;
        this.date = date;
        this.time = time;
        this.id = id;
        this.car_id = car_id;
        this.work_price = work_price;
        this.comment = comment;
        this.description = description;
        this.type = type;
        this.photos = photos;
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

    public int getWork_price() {
        return work_price;
    }

    public void setWork_price(int work_price) {
        this.work_price = work_price;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public ArrayList<Bitmap> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<Bitmap> photos) {
        this.photos = photos;
    }
}
