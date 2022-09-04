package com.example.automechapp;

import static com.example.automechapp.DatabaseInfo.*;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.util.ArrayList;

public class GetOwners extends Thread {
    private final ArrayList<Owner> owners = new ArrayList<>();
    Context context;
    String[] projection;
    String selection;
    String sortOrder;

    public GetOwners(Context context, String selection) {
        this.context = context;
        this.projection = new String[] {
                OWNER_ID,
                USERNAME,
                SURNAME,
                DRIVER_LICENSE,
                PATRONYMIC,
                DATE_OF_BIRTH,
                REGION,
                ISSUING_REGION,
                CATEGORIES,
                PASSPORT_SERIES,
                PASSPORT_NUMBER
        };
        this.selection = selection;
        this.sortOrder = USERNAME + ", " + SURNAME + " DESC";
    }

    @Override
    public void run() {
        DatabaseInterface databaseInterface = new DatabaseInterface(context);
        GetData getData = databaseInterface.GetData(
                OWNERS_TABLE,
                projection,
                selection,
                sortOrder
        );

        try {
            getData.join();
            Cursor cursor =  getData.getCursor();
            int[] indexes = new int[] {
                    cursor.getColumnIndex(projection[0]),
                    cursor.getColumnIndex(projection[1]),
                    cursor.getColumnIndex(projection[2]),
                    cursor.getColumnIndex(projection[3]),
                    cursor.getColumnIndex(projection[4]),
                    cursor.getColumnIndex(projection[5]),
                    cursor.getColumnIndex(projection[6]),
                    cursor.getColumnIndex(projection[7]),
                    cursor.getColumnIndex(projection[8]),
                    cursor.getColumnIndex(projection[9]),
                    cursor.getColumnIndex(projection[10])
            };

            if (selection != null) {
                while (cursor.moveToNext()) {
                    owners.add(new Owner(
                            cursor.getInt(indexes[0]),
                            cursor.getString(indexes[1]),
                            cursor.getString(indexes[2]),
                            cursor.getInt(indexes[3])
                    ));
                }
            }
            else {
                while (cursor.moveToNext()) {
                    owners.add(new Owner(
                            cursor.getInt(indexes[0]),
                            cursor.getString(indexes[1]),
                            cursor.getString(indexes[2]),
                            cursor.getInt(indexes[3]),
                            cursor.getString(indexes[4]),
                            cursor.getString(indexes[5]),
                            cursor.getString(indexes[6]),
                            cursor.getString(indexes[7]),
                            cursor.getString(indexes[8]),
                            cursor.getInt(indexes[9]),
                            cursor.getInt(indexes[10])
                    ));
                }

            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Owner> getOwnersList() {
        return owners;
    }
}
