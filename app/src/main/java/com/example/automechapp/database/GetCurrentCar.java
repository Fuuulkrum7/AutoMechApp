package com.example.automechapp.database;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.automechapp.car.Car;
import com.example.automechapp.car.CarActivity;
import com.example.automechapp.database.DatabaseInfo;
import com.example.automechapp.database.GetCars;

public class GetCurrentCar extends Thread {
    private final CarActivity carActivity;
    Context context;
    int id;

    public GetCurrentCar(CarActivity carActivity, int id) {
        this.carActivity = carActivity;
        this.context = carActivity;
        this.id = id;
    }

    @Override
    public void run() {
        GetCars getCars = new GetCars(
                context,
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
