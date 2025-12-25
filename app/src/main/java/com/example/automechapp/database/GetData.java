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
import com.example.automechapp.owner.Owner;

import java.util.ArrayList;

public class GetData<T> extends Thread {
    String table;
    SQLiteDatabase db;
    String[] projection;
    String selection;
    String sortOrder;
    Context context;
    final ArrayList<T> data = new ArrayList<>();
    Runnable onDone = null;

    public GetData(Context ctx, String table, String[] projection, String selection, String sortOrder) {
        this.context = ctx;
        this.table = table;
        this.projection = projection;
        this.selection = selection;
        this.sortOrder = sortOrder;
    }

    public void moveDb(SQLiteDatabase db) {
        this.db = db;
    }

    public void setRunnable(Runnable onDone) {
        this.onDone = onDone;
    }

    void onFinish() {
        if (onDone == null) {
            return;
        }
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(onDone);
    }

    public Cursor getCursor() {
        try {
            return db.query(
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
            Log.d(MainActivity.TAG, e.toString());

            new Handler(Looper.getMainLooper()).post(() -> {
                Context context = MainActivity.getContext();
                if (context == null) {
                    context = CarActivity.getContext();
                }
                Toast toast = Toast.makeText(context,
                        "Не удалось получить данные", Toast.LENGTH_SHORT);
                toast.show();
            });
        }
        return null;
    }

    public void close() {
        if (db != null)
            db.close();
    }

    public ArrayList<T> getData() {
        return data;
    }
}
