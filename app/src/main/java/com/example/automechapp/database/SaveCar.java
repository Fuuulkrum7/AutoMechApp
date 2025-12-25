package com.example.automechapp.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;

import com.example.automechapp.camera_utils.ImageUtil;
import com.example.automechapp.car.CarActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SaveCar extends AddData {
    public SaveCar(CarActivity carActivity, Context context, ContentValues contentValues) {
        super(carActivity, context, contentValues,
                DatabaseInfo.CARS_TABLE, DatabaseInfo.CAR_PHOTOS_TABLE, DatabaseInfo.CAR_ID);
    }
}
