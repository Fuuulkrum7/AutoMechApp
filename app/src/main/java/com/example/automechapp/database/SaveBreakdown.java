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

public class SaveBreakdown extends Thread {
    private final BreakdownActivity breakdownActivity;
    DatabaseInterface databaseInterface;
    ContentValues contentValues;

    public SaveBreakdown(BreakdownActivity breakdownActivity, Context context, ContentValues contentValues) {
        this.breakdownActivity = breakdownActivity;
        databaseInterface = new DatabaseInterface(context);
        this.contentValues = contentValues;
    }

    @Override
    public void run() {
        AddData addData = databaseInterface.addData(contentValues, DatabaseInfo.BREAKDOWNS_TABLE);
        try {
            addData.join();
            breakdownActivity.setId((int) addData.getIndex());


            for (Bitmap b : breakdownActivity.bitmaps) {
                byte[] photo = ImageUtil.getBitmapAsByteArray(b);

                ContentValues contentValues = new ContentValues();

                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Calendar c = Calendar.getInstance();
                String date = sdf.format(c.getTime());

                contentValues.put(DatabaseInfo.STANDARD_DATE, date);
                contentValues.put(DatabaseInfo.BREAKDOWN_ID, breakdownActivity.getId());
                contentValues.put(DatabaseInfo.STANDARD_PHOTO, photo);

                databaseInterface.addData(contentValues, DatabaseInfo.BREAKDOWNS_PHOTOS_TABLE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
