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

public class DeleteData extends Thread {
    String table;
    SQLiteDatabase db;
    String[] projection;
    String selection;

    public DeleteData(SQLiteDatabase db, String table, String[] projection, String selection) {
        this.db = db;
        this.table = table;
        this.projection = projection;
        this.selection = selection;
    }

    @SuppressLint("Recycle")
    @Override
    public void run() {
        try {
            db.delete(
                    table,
                    selection,
                    projection
            );
        } catch (Exception e) {
            // Если что-то пошло не так
            Log.d(MainActivity.TAG, e.toString());

            new Handler(Looper.getMainLooper()).post(() -> {
                Context context = MainActivity.getContext();
                if (context == null) {
                    context = CarActivity.getContext();
                }
                Toast toast = Toast.makeText(context,
                        "Не удалось удалить данные", Toast.LENGTH_SHORT);
                toast.show();
            });
        }
    }
}
