package com.example.automechapp;

import static com.example.automechapp.DatabaseInfo.*;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DatabaseInterface extends SQLiteOpenHelper {
    // Данные по бд
    public static final int DATABASE_VERSION = 1;

    // Инициализация, ничего интересного
    public DatabaseInterface(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

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

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        clearAllTables(db);
        onCreate(db);
    }

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

    public void AddData(ContentValues values, String table){
        SQLiteDatabase database = getWritableDatabase();
        AddData addData = new AddData(values, database, table);
        addData.start();
    }

    public GetData GetData(String table, int id, String[] projection, String selection, String sortOrder) {
        SQLiteDatabase db = getReadableDatabase();
        GetData getData = new GetData(db, table, id, projection, selection, sortOrder);
        getData.start();

        return getData;
    }

    class GetData extends Thread {
        int id;
        String table;
        SQLiteDatabase db;
        String[] projection;
        String selection;
        String sortOrder;
        ArrayList<HashMap<String, String>> data = new ArrayList<>();

        public GetData (SQLiteDatabase db, String table, int id, String[] projection, String selection, String sortOrder){
            this.db = db;
            this.table = table;
            this.id = id;
            this.projection = projection;
            this.selection = selection;
            this.sortOrder = sortOrder;
        }

        @SuppressLint("Recycle")
        @Override
        public void run() {
            Cursor cursor;

            try {
                cursor = db.query(
                        table,
                        projection,
                        selection,
                        null,
                        null,
                        null,
                        sortOrder
                );
            }
            catch (Exception e) {
                // Если что-то пошло не так
                Log.d("TEST", e.toString());

                Toast toast = Toast.makeText(MainActivity.getContext(),
                        "Не удалось получить данные", Toast.LENGTH_SHORT);

                toast.show();

                return;
            }

            int[] indexes = new int[projection.length];

            for (int i = 0; i < indexes.length; i++) {
                indexes[i] = cursor.getColumnIndex(projection[i]);
            }
        }

        public ArrayList<HashMap<String, String>> getData() {
            return data;
        }
    }
}
