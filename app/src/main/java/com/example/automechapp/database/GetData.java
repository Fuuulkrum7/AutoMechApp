package com.example.automechapp.database;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.automechapp.MainActivity;
import com.example.automechapp.car.CarActivity;

public class GetData extends Thread {
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

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Context context = MainActivity.getContext();
                    if (context == null) {
                        context = CarActivity.getContext();
                    }
                    Toast toast = Toast.makeText(context,
                            "Не удалось получить данные", Toast.LENGTH_SHORT);
                    toast.show();
                }
            });

        }
    }

    public Cursor getCursor() {
        return cursor;
    }
}
