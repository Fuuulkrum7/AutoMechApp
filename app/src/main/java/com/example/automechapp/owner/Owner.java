package com.example.automechapp.owner;

public class Owner {
    private int id;
    private String username;
    private String patronymic;
    private String surname;
    private String dob;
    private int driver_license;
    private String region;
    private String issuing_region;
    private String categories;
    private int passport_series;
    private int passport_number;

    public Owner(int id, String username, String surname, int driver_license, String patronymic, String dob, String region, String issuing_region, String categories, int passport_series, int passport_number) {
        this.id = id;
        this.username = username;
        this.patronymic = patronymic;
        this.surname = surname;
        this.dob = dob;
        this.driver_license = driver_license;
        this.region = region;
        this.issuing_region = issuing_region;
        this.categories = categories;
        this.passport_series = passport_series;
        this.passport_number = passport_number;
    }

    public Owner(int id, String username, String surname, int driver_license) {
        this.id = id;
        this.username = username;
        this.surname = surname;
        this.driver_license = driver_license;
    }

    public String getOwnerText() {
        return surname + " " + username + "\n" + Integer.toString(driver_license);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public int getDriver_license() {
        return driver_license;
    }

    public void setDriver_license(int driver_license) {
        this.driver_license = driver_license;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getIssuing_region() {
        return issuing_region;
    }

    public void setIssuing_region(String issuing_region) {
        this.issuing_region = issuing_region;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public int getPassport_series() {
        return passport_series;
    }

    public void setPassport_series(int passport_series) {
        this.passport_series = passport_series;
    }

    public int getPassport_number() {
        return passport_number;
    }

    public void setPassport_number(int passport_number) {
        this.passport_number = passport_number;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }
}
