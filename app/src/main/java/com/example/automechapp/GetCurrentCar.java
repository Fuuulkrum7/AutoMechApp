package com.example.automechapp;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

class GetCurrentCar extends Thread {
    private final CarActivity carActivity;
    Context context;
    int id;

    GetCurrentCar(CarActivity carActivity, Context context, int id) {
        this.carActivity = carActivity;
        this.context = context;
        this.id = id;
    }

    @Override
    public void run() {
        GetCars getCars = new GetCars(
                context,
                new String[]{
                        DatabaseInfo.CAR_ID,
                        DatabaseInfo.OWNER_ID,
                        DatabaseInfo.CAR_MODEL,
                        DatabaseInfo.CAR_MANUFACTURE,
                        DatabaseInfo.CAR_NAME,
                        DatabaseInfo.CAR_PHOTO,
                        DatabaseInfo.CAR_YEAR,
                        DatabaseInfo.CAR_PRICE,
                        DatabaseInfo.VIN,
                        DatabaseInfo.ENGINE_MODEL,
                        DatabaseInfo.ENGINE_NUMBER,
                        DatabaseInfo.ENGINE_VOLUME,
                        DatabaseInfo.CAR_YEAR,
                        DatabaseInfo.STATE_CAR_NUMBER,
                        DatabaseInfo.TAX,
                        DatabaseInfo.COLOR,
                        DatabaseInfo.HORSEPOWER
                },
                DatabaseInfo.CAR_ID + " = '" + id + "'",
                null,
                id
        );
        getCars.start();

        try {
            getCars.join();
            if (getCars.getData() != null) {
                Car car = getCars.getData().get(0);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        carActivity.setData(car);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
