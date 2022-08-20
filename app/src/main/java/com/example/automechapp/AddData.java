package com.example.automechapp;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

class AddData extends Thread {
    ContentValues values;
    SQLiteDatabase db;
    String tableName;

    AddData(ContentValues values, SQLiteDatabase db, String tableName) {
        this.db = db;
        this.values = values;
        this.tableName = tableName;
    }

    @Override
    public void run() {
// Добавляем в бд
        try {
            db.insert(tableName, null, values);
        }
        // Если что-то пошло не так, то вот
        catch (Exception e) {
            Log.d("TEST", e.toString());
            Toast toast = Toast.makeText(MainActivity.getContext(),
                    "Не удалось добавить данные", Toast.LENGTH_SHORT);

            toast.show();
        }
    }
}
