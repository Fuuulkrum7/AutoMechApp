package com.example.automechapp;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

class GetData extends Thread {
    String table;
    SQLiteDatabase db;
    String[] projection;
    String selection;
    String sortOrder;
    private Cursor cursor = null;

    public GetData(SQLiteDatabase db, String table, String[] projection, String selection, String sortOrder) {
        this.db = db;
        this.table = table;
        this.projection = projection;
        this.selection = selection;
        this.sortOrder = sortOrder;
    }

    @SuppressLint("Recycle")
    @Override
    public void run() {
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
        } catch (Exception e) {
            // Если что-то пошло не так
            Log.d("TEST", e.toString());

            Toast toast = Toast.makeText(MainActivity.getContext(),
                    "Не удалось получить данные", Toast.LENGTH_SHORT);

            toast.show();
        }
    }

    public Cursor getCursor() {
        return cursor;
    }
}
