package com.example.automechapp.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;

import androidx.appcompat.widget.ThemeUtils;

import com.example.automechapp.breakdown.BreakdownActivity;
import com.example.automechapp.camera_utils.ImageUtil;
import com.example.automechapp.car.CarActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SaveBreakdown extends AddData {
    public SaveBreakdown(BreakdownActivity breakdownActivity, Context context,
                         ContentValues contentValues) {
        super(breakdownActivity, context, contentValues, DatabaseInfo.BREAKDOWNS_TABLE,
                DatabaseInfo.BREAKDOWNS_PHOTOS_TABLE, DatabaseInfo.BREAKDOWN_ID);
    }
}
