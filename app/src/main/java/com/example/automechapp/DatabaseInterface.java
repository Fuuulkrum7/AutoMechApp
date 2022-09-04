package com.example.automechapp;

import static com.example.automechapp.DatabaseInfo.*;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

public class DatabaseInterface extends SQLiteOpenHelper {
    // Данные по бд
    public static final int DATABASE_VERSION = 5;

    // Инициализация, ничего интересного
    public DatabaseInterface(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Инициализируем бд
    @Override
    public void onCreate(SQLiteDatabase db) {
        for (String s : Arrays.asList(
                CREATE_OWNERS_TABLE,
                CREATE_CARS_TABLE,
                CREATE_BREAKDOWNS_TABLE,
                CREATE_DETAILS_TABLE,
                CREATE_CAR_PHOTOS_TABLE,
                CREATE_BREAKDOWNS_PHOTOS_TABLE,
                CREATE_DETAILS_PHOTOS_TABLE,
                CREATE_DOCUMENTS_TABLE)) {
            db.execSQL(s);
        }
    }

    // Обновление бд
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        clearAllTables(db);
        onCreate(db);
    }

    // Удвление таблиц
    private void clearAllTables(SQLiteDatabase db) {
        db.execSQL(DELETE_OWNERS_TABLE);
        db.execSQL(DELETE_CARS_TABLE);
        db.execSQL(DELETE_BREAKDOWNS_TABLE);
        db.execSQL(DELETE_DETAILS_TABLE);
        db.execSQL(DELETE_CAR_PHOTOS_TABLE);
        db.execSQL(DELETE_BREAKDOWNS_PHOTOS_TABLE);
        db.execSQL(DELETE_DETAILS_PHOTOS_TABLE);
        db.execSQL(DELETE_DOCUMENTS_TABLE);
    }

    // Метод добавления данных в бд
    public AddData addData(ContentValues values, String table){
        SQLiteDatabase database = getWritableDatabase();
        AddData add_data = new AddData(values, database, table);
        add_data.start();

        return add_data;
    }

    // Получение данных из бд
    public GetData GetData(String table, String[] projection, String selection, String sortOrder) {
        SQLiteDatabase db = getReadableDatabase();
        GetData getData = new GetData(db, table, projection, selection, sortOrder);
        getData.start();

        return getData;
    }
}
