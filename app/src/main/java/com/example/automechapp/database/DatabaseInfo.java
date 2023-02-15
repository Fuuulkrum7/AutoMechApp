package com.example.automechapp.database;

import android.provider.BaseColumns;

final public class DatabaseInfo implements BaseColumns {
    public static final String DATABASE_NAME = "AutoMechApp.db";

    public final static String OWNERS_TABLE = "owner";
    public final static String CARS_TABLE = "cars";
    public final static String BREAKDOWNS_TABLE = "breakdowns";
    public final static String DETAILS_TABLE = "details";
    public final static String BREAKDOWNS_PHOTOS_TABLE = "breakdowns_photos";
    public final static String CAR_PHOTOS_TABLE = "car_photos";
    public final static String DOCUMENTS_TABLE = "documents";
    public final static String DETAILS_PHOTOS_TABLE = "details_photos";

    public final static String STANDARD_ID = "ID";
    public final static String STANDARD_DATE = "date";
    public final static String STANDARD_COMMENT = "comment";
    public final static String STANDARD_DESCRIPTION = "description";
    public final static String STANDARD_PHOTO = "photo";

    public final static String OWNER_ID = "owner_id";
    public final static String USERNAME = "username";
    public final static String SURNAME = "surname";
    public final static String PATRONYMIC = "patronymic";
    public final static String DATE_OF_BIRTH = "DOB";
    public final static String DRIVER_LICENSE = "driver_license";
    public final static String REGION = "region";
    public final static String ISSUING_REGION = "issuing_region";
    public final static String CATEGORIES = "categories";
    public final static String PASSPORT_SERIES = "passport_series";
    public final static String PASSPORT_NUMBER = "passport_number";
    public final static String HORSEPOWER = "horsepower";

    public final static String CAR_NAME = "car_name";
    public final static String CAR_YEAR = "car_year";
    public final static String CAR_MANUFACTURE = "car_manufacture";
    public final static String CAR_MODEL = "car_model";
    public final static String CAR_PRICE = "car_price";
    public final static String TAX = "tax";
    public final static String COLOR = "color";
    public final static String VIN = "VIN";
    public final static String STATE_CAR_NUMBER = "state_car_number";
    public final static String ENGINE_VOLUME = "engine_volume";
    public final static String ENGINE_NUMBER = "engine_number";
    public final static String ENGINE_MODEL = "engine_model";
    public final static String CAR_PHOTO = "car_photo";

    public final static String CAR_ID = "car_id";
    public final static String BREAKDOWN_NAME = "breakdown_name";
    public final static String WORK_PRICE = "work_price";
    public final static String EDIT_TIME = "edit_time";
    public final static String BREAKDOWN_TYPE = "type";
    public final static String WORK_TIME = "work_time";
    public final static String BREAKDOWN_PHOTO = "breakdown_photo";

    public final static String BREAKDOWN_ID = "breakdown_id";
    public final static String DETAIL_MANUFACTURE = "detail_manufacture";
    public final static String DETAIL_MODEL = "detail_model";
    public final static String DETAIL_NUMBER = "detail_number";

    public static final String CREATE_OWNERS_TABLE =
            "CREATE TABLE " + OWNERS_TABLE + " (" +
            OWNER_ID + " INTEGER PRIMARY KEY," +
            USERNAME + " TEXT," +
            SURNAME + " TEXT," +
            PATRONYMIC + " TEXT," +
            DATE_OF_BIRTH + " NUMERIC," +
            DRIVER_LICENSE + " INTEGER," +
            REGION + " TEXT," +
            ISSUING_REGION + " TEXT," +
            CATEGORIES + " TEXT," +
            PASSPORT_SERIES + " INTEGER," +
            PASSPORT_NUMBER + " INTEGER);";

    public static final String CREATE_CARS_TABLE =
            "CREATE TABLE " + CARS_TABLE + " (" +
            OWNER_ID + " INTEGER," +
            CAR_ID + " INTEGER PRIMARY KEY," +
            CAR_NAME + " TEXT," +
            CAR_YEAR + " INTEGER," +
            CAR_MANUFACTURE + " TEXT," +
            CAR_MODEL + " TEXT," +
            CAR_PRICE + " INTEGER," +
            CAR_PHOTO + " BLOB," +
            TAX + " INTEGER," +
            COLOR + " TEXT," +
            VIN + " TEXT," +
            STATE_CAR_NUMBER + " INTEGER," +
            HORSEPOWER + " INTEGER," +
            ENGINE_NUMBER + " TEXT," +
            ENGINE_MODEL + " TEXT," +
            STANDARD_DATE + " NUMERIC," +
            ENGINE_VOLUME + " REALs);";

    public static final String CREATE_BREAKDOWNS_TABLE =
            "CREATE TABLE " + BREAKDOWNS_TABLE + " (" +
            CAR_ID + " INTEGER," +
            STANDARD_ID + " INTEGER PRIMARY KEY," +
            BREAKDOWN_NAME + " TEXT," +
            WORK_PRICE + " INTEGER," +
            STANDARD_DATE + " NUMERIC," +
            EDIT_TIME + " NUMERIC," +
            STANDARD_COMMENT + " TEXT," +
            STANDARD_DESCRIPTION + " TEXT," +
            BREAKDOWN_PHOTO + " BLOB," +
            BREAKDOWN_TYPE + " INTEGER," +
            WORK_TIME + " INTEGER);";


    public static final String CREATE_DETAILS_TABLE =
            "CREATE TABLE " + DETAILS_TABLE + " (" +
            BREAKDOWN_ID + " INTEGER," +
            STANDARD_ID + " INTEGER PRIMARY KEY," +
            STANDARD_COMMENT + " TEXT," +
            STANDARD_DESCRIPTION + " TEXT," +
            STANDARD_DATE + " NUMERIC," +
            DETAIL_MANUFACTURE + " TEXT," +
            DETAIL_MODEL + " TEXT," +
            DETAIL_NUMBER + " TEXT);";

    public static final String CREATE_CAR_PHOTOS_TABLE =
            "CREATE TABLE " + CAR_PHOTOS_TABLE + " (" +
                    CAR_ID + " INTEGER," +
                    STANDARD_ID + " INTEGER PRIMARY KEY," +
                    STANDARD_PHOTO + " BLOB," +
                    STANDARD_DATE + " NUMERIC);";

    public static final String CREATE_BREAKDOWNS_PHOTOS_TABLE =
            "CREATE TABLE " + BREAKDOWNS_PHOTOS_TABLE + " (" +
                    BREAKDOWN_ID + " INTEGER," +
                    STANDARD_ID + " INTEGER PRIMARY KEY," +
                    STANDARD_PHOTO + " BLOB," +
                    STANDARD_DESCRIPTION + " STRING," +
                    STANDARD_DATE + " NUMERIC);";

    public static final String CREATE_DETAILS_PHOTOS_TABLE =
            "CREATE TABLE " + DETAILS_PHOTOS_TABLE + " (" +
                    CAR_ID + " INTEGER," +
                    STANDARD_ID + " INTEGER PRIMARY KEY," +
                    STANDARD_PHOTO + " BLOB," +
                    STANDARD_DATE + " NUMERIC);";


    public static final String CREATE_DOCUMENTS_TABLE =
            "CREATE TABLE " + DOCUMENTS_TABLE + " (" +
                    CAR_ID + " INTEGER," +
                    STANDARD_ID + " INTEGER PRIMARY KEY," +
                    STANDARD_PHOTO + " BLOB," +
                    STANDARD_DATE + " NUMERIC);";

    public static final String DELETE_OWNERS_TABLE =
            "DROP TABLE IF EXISTS " + OWNERS_TABLE;

    public static final String DELETE_CARS_TABLE =
            "DROP TABLE IF EXISTS " + CARS_TABLE;

    public static final String DELETE_BREAKDOWNS_TABLE =
            "DROP TABLE IF EXISTS " + BREAKDOWNS_TABLE;

    public static final String DELETE_DETAILS_TABLE =
            "DROP TABLE IF EXISTS " + DETAILS_TABLE;

    public static final String DELETE_CAR_PHOTOS_TABLE =
            "DROP TABLE IF EXISTS " + CAR_PHOTOS_TABLE;

    public static final String DELETE_BREAKDOWNS_PHOTOS_TABLE =
            "DROP TABLE IF EXISTS " + BREAKDOWNS_PHOTOS_TABLE;

    public static final String DELETE_DETAILS_PHOTOS_TABLE =
            "DROP TABLE IF EXISTS " + DETAILS_PHOTOS_TABLE;

    public static final String DELETE_DOCUMENTS_TABLE =
            "DROP TABLE IF EXISTS " + DOCUMENTS_TABLE;

    private DatabaseInfo() {}
}
