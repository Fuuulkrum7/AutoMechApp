package com.example.automechapp.database;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;

public class DeleteCars extends Thread {
    DatabaseInterface database;

    ArrayList<Integer> cars_id;
    Context context;

    public DeleteCars(DatabaseInterface database, ArrayList<Integer> cars_id){
        this.database = database;
        this.cars_id = cars_id;
    }

    public DeleteCars(Context context, ArrayList<Integer> cars_id) {
        this(new DatabaseInterface(context), cars_id);
        this.context = context;
    }

    @Override
    public void run(){
        if (cars_id.size() == 0)
            return;
        String[] sarr = Arrays.stream(cars_id.toArray()).map(String::valueOf).toArray(String[]::new);
        database.deleteData(DatabaseInfo.CARS_TABLE, null, DatabaseInfo.CAR_ID + " in (" + String.join(", ", sarr) + ")");
        database.deleteData(DatabaseInfo.BREAKDOWNS_TABLE, null, DatabaseInfo.CAR_ID + " in (" + String.join(", ", sarr) + ")");
    }
}
