package com.example.automechapp.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

import com.example.automechapp.camera_utils.ImageUtil;
import com.example.automechapp.camera_utils.PhotoWorker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddData extends Thread {
    private final PhotoWorker activity;
    DatabaseInterface databaseInterface;
    ContentValues contentValues;
    String photoTable, tableID;
    // Данные, которые мы добавим в бд
    ContentValues values;
    // База данных
    SQLiteDatabase db;
    // Имя таблицы
    String tableName;

    AddData(PhotoWorker activity, Context context,
             ContentValues values, String tableName,
             String photoTable, String tableID) {
        this.values = values;
        this.tableName = tableName;
        this.activity = activity;
        this.databaseInterface = new DatabaseInterface(context);
        this.photoTable = photoTable;
        this.tableID = tableID;
    }

    @Override
    public void run() {
        long index = databaseInterface.addData(values, tableName);
        try {
            activity.setId((int) index);

            for (Bitmap b : activity.bitmaps) {
                byte[] photo = ImageUtil.getBitmapAsByteArray(b);

                ContentValues contentValues = new ContentValues();

                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Calendar c = Calendar.getInstance();
                String date = sdf.format(c.getTime());

                contentValues.put(DatabaseInfo.STANDARD_DATE, date);
                contentValues.put(tableID, activity.getId());
                contentValues.put(DatabaseInfo.STANDARD_PHOTO, photo);

                databaseInterface.addData(contentValues, photoTable);
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
