package com.example.automechapp.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.automechapp.MainActivity;
import com.example.automechapp.car.CarActivity;

public class UpdateData extends Thread {
    String table;
    SQLiteDatabase db;
    String[] whereArgs;
    String whereClause;
    ContentValues values;

    public UpdateData(SQLiteDatabase db, String table, ContentValues values, String[] whereArgs, String whereClause) {
        this.db = db;
        this.table = table;
        this.whereArgs = whereArgs;
        this.whereClause = whereClause;
        this.values = values;
    }

    @SuppressLint("Recycle")
    @Override
    public void run() {
        try {
            db.update(
                    table,
                    values,
                    whereClause,
                    whereArgs
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
                        "Не удалось обновить данные", Toast.LENGTH_SHORT);
                toast.show();
            });

        }
    }
}
