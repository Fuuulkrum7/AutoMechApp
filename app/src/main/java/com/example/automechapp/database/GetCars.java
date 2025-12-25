package com.example.automechapp.database;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import static com.example.automechapp.database.DatabaseInfo.*;

import com.example.automechapp.MainActivity;
import com.example.automechapp.camera_utils.ImageUtil;
import com.example.automechapp.car.Car;

import java.util.ArrayList;

public class GetCars extends GetData<Car> {
    DatabaseInterface database;
    int id = -1;

    public GetCars(Context context, String selection, String sortOrder, int id){
        super(
                context,
                DatabaseInfo.CARS_TABLE,
                buildCarProjection(id > -1),
                selection,
                sortOrder
        );
        this.database = new DatabaseInterface(context);
        this.id = id;
    }

    public GetCars(Context context, String selection, String sortOrder) {
        this(context, selection, sortOrder, -1);
    }

    private static String[] buildCarProjection(boolean full) {
        if (full) {
            return new String[]{
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
            };
        }

        return new String[]{
                DatabaseInfo.CAR_NAME,
                DatabaseInfo.CAR_MANUFACTURE,
                DatabaseInfo.CAR_MODEL,
                DatabaseInfo.CAR_ID,
                DatabaseInfo.CAR_PHOTO,
                DatabaseInfo.OWNER_ID
        };
    }


    @Override
    public void run(){
        database.getData(this);
        try {
            Cursor cursor = getCursor();
            if (cursor == null) {
                new Handler(Looper.getMainLooper()).post(
                        () -> Toast.makeText(context, "ошибка", Toast.LENGTH_SHORT).show());
                return;
            }

            int[] indexes = new int[] {
                    cursor.getColumnIndex(CAR_NAME),
                    cursor.getColumnIndex(CAR_MANUFACTURE),
                    cursor.getColumnIndex(CAR_MODEL),
                    cursor.getColumnIndex(CAR_ID),
                    cursor.getColumnIndex(CAR_PHOTO),
                    cursor.getColumnIndex(OWNER_ID),
                    cursor.getColumnIndex(CAR_YEAR),
                    cursor.getColumnIndex(CAR_PRICE),
                    cursor.getColumnIndex(TAX),
                    cursor.getColumnIndex(COLOR),
                    cursor.getColumnIndex(VIN),
                    cursor.getColumnIndex(STATE_CAR_NUMBER),
                    cursor.getColumnIndex(ENGINE_VOLUME),
                    cursor.getColumnIndex(ENGINE_NUMBER),
                    cursor.getColumnIndex(ENGINE_MODEL),
                    cursor.getColumnIndex(HORSEPOWER)

            };

            if (id > -1) {
                GetAnyPhotos task = new GetAnyPhotos(context, id, CAR_PHOTOS_TABLE, CAR_ID);
                task.run();
                ArrayList<Bitmap> photos = task.getPhotos();

                while (cursor.moveToNext()) {
                    data.add(new Car(
                            cursor.getString(indexes[0]),
                            cursor.getString(indexes[1]),
                            cursor.getString(indexes[2]),
                            cursor.getInt(indexes[3]),
                            ImageUtil.getByteArrayAsBitmap(cursor.getBlob(indexes[4])),
                            cursor.getInt(indexes[5]),
                            cursor.getInt(indexes[6]),
                            cursor.getInt(indexes[7]),
                            cursor.getInt(indexes[8]),
                            cursor.getString(indexes[9]),
                            cursor.getString(indexes[10]),
                            cursor.getString(indexes[11]),
                            cursor.getFloat(indexes[12]),
                            cursor.getString(indexes[13]),
                            cursor.getString(indexes[14]),
                            cursor.getInt(indexes[15]),
                            photos));
                }
            }
            else {
                while (cursor.moveToNext()) {
                    data.add(new Car(
                            cursor.getString(indexes[0]),
                            cursor.getString(indexes[1]),
                            cursor.getString(indexes[2]),
                            cursor.getInt(indexes[3]),
                            ImageUtil.getByteArrayAsBitmap(cursor.getBlob(indexes[4])),
                            cursor.getInt(indexes[5])
                    ));
                }
            }
            cursor.close();
            close();
            onFinish();
        }
        catch (Exception e) {
            close();
        }
    }
}
