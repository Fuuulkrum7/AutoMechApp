package com.example.automechapp.database;

import static com.example.automechapp.database.DatabaseInfo.*;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.example.automechapp.owner.Owner;

import java.util.ArrayList;

public class GetOwners extends GetData<Owner> {

    public GetOwners(Context context, String selection) {
        super(
            context,
            OWNERS_TABLE,
            new String[] {
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
            },
            selection,
            USERNAME + ", " + SURNAME + " DESC"
        );
    }

    @Override
    public void run() {
        DatabaseInterface databaseInterface = new DatabaseInterface(context);
        databaseInterface.getData(this);

        try (Cursor cursor = getCursor()) {
            int[] indexes = new int[]{
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
                    data.add(new Owner(
                            cursor.getInt(indexes[0]),
                            cursor.getString(indexes[1]),
                            cursor.getString(indexes[2]),
                            cursor.getInt(indexes[3])
                    ));
                }
            } else {
                while (cursor.moveToNext()) {
                    data.add(new Owner(
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
            cursor.close();
            onFinish();
        } catch (Exception e) {
            Log.e("GET_OWNERS", "Get owners failed");
            e.printStackTrace();
        } finally {
            close();
        }
    }
}
