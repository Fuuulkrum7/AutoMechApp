package com.example.automechapp;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.widget.Toast;

import static com.example.automechapp.DatabaseInfo.*;

import java.util.ArrayList;

public class GetCars extends Thread {
    DatabaseInterface database;
    String[] projection;
    String selection;
    String sortOrder;
    ArrayList<Car> cars = new ArrayList<Car>();
    int car_id = -1;

    public GetCars(DatabaseInterface database, String[] projection, String selection, String sortOrder){
        this.database = database;
        this.projection = projection;
        this.selection = selection;
        this.sortOrder = sortOrder;
    }

    public GetCars(Context context, String[] projection, String selection, String sortOrder) {
        this(new DatabaseInterface(context), projection, selection, sortOrder);
    }

    public GetCars(Context context, String[] projection, String selection, String sortOrder, int car_id) {
        this(context, projection, selection, sortOrder);
        this.car_id = car_id;
    }

    @Override
    public void run(){
        GetData getData = database.GetData(DatabaseInfo.CARS_TABLE, projection, selection, sortOrder);
        try {
            getData.join();
            Cursor cursor = getData.getCursor();
            if (cursor == null) {
                Toast.makeText(MainActivity.getContext(), "Ошибка", Toast.LENGTH_SHORT).show();
                return;
            }

            // TODO добавить получение фото
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
                    cursor.getColumnIndex(ENGINE_VOLUME)

            };

            if (projection.length > Car.defaultCar) {
                GetData getData1 = database.GetData(
                        CAR_PHOTOS_TABLE,
                        new String[] {
                                STANDARD_ID,
                                STANDARD_PHOTO
                        },
                        CAR_ID + " = '" + car_id + "'",
                        STANDARD_DATE + " ASC"
                );

                getData1.start();
                Cursor photos_cursor;
                ArrayList<Bitmap> photos = new ArrayList<Bitmap>();

                try {
                    getData1.join();
                    photos_cursor = getData1.getCursor();
                    int photoIndex = photos_cursor.getColumnIndex(STANDARD_PHOTO);

                    while (cursor.moveToNext()) {
                        photos.add(ImageUtil.getByteArrayAsBitmap(
                                photos_cursor.getBlob(photoIndex)
                        ));
                    }
                }
                catch (Exception e) {
                    Toast.makeText(MainActivity.getContext(), "Не удалось загрузить фото", Toast.LENGTH_SHORT).show();
                    return;
                }

                cursor.moveToFirst();

                cars.add(new Car(
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
                        cursor.getInt(indexes[12]),
                        cursor.getString(indexes[13]),
                        cursor.getString(indexes[14]),
                        photos));
            }
            else {
                while (cursor.moveToNext()) {
                    cars.add(new Car(
                            cursor.getString(indexes[0]),
                            cursor.getString(indexes[1]),
                            cursor.getString(indexes[2]),
                            cursor.getInt(indexes[3]),
                            ImageUtil.getByteArrayAsBitmap(cursor.getBlob(indexes[4]))
                    ));
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Car> getData() {
        return cars;
    }
}
