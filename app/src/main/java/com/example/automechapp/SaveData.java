package com.example.automechapp;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;

import java.text.SimpleDateFormat;
import java.util.Calendar;

class SaveData extends Thread {
    private final CarActivity carActivity;
    DatabaseInterface databaseInterface;

    public SaveData(CarActivity carActivity, Context context) {
        this.carActivity = carActivity;
        databaseInterface = new DatabaseInterface(context);
    }

    @Override
    public void run() {
        AddData addData = databaseInterface.addData(carActivity.getValues(), DatabaseInfo.CARS_TABLE);
        try {
            addData.join();
            carActivity.setId((int) addData.getIndex());


            for (Bitmap b : carActivity.bitmaps) {
                byte[] photo = ImageUtil.getBitmapAsByteArray(b);

                ContentValues contentValues = new ContentValues();

                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Calendar c = Calendar.getInstance();
                String date = sdf.format(c.getTime());

                contentValues.put(DatabaseInfo.STANDARD_DATE, date);
                contentValues.put(DatabaseInfo.CAR_ID, carActivity.getId());
                contentValues.put(DatabaseInfo.STANDARD_PHOTO, photo);

                databaseInterface.addData(contentValues, DatabaseInfo.CAR_PHOTOS_TABLE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
