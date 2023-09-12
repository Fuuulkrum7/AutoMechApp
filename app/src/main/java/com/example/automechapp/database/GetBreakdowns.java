package com.example.automechapp.database;

import static com.example.automechapp.database.DatabaseInfo.BREAKDOWNS_PHOTOS_TABLE;
import static com.example.automechapp.database.DatabaseInfo.BREAKDOWNS_TABLE;
import static com.example.automechapp.database.DatabaseInfo.BREAKDOWN_ID;
import static com.example.automechapp.database.DatabaseInfo.BREAKDOWN_NAME;
import static com.example.automechapp.database.DatabaseInfo.BREAKDOWN_PHOTO;
import static com.example.automechapp.database.DatabaseInfo.BREAKDOWN_STATE;
import static com.example.automechapp.database.DatabaseInfo.BREAKDOWN_TYPE;
import static com.example.automechapp.database.DatabaseInfo.CARS_TABLE;
import static com.example.automechapp.database.DatabaseInfo.CAR_ID;
import static com.example.automechapp.database.DatabaseInfo.CAR_MANUFACTURE;
import static com.example.automechapp.database.DatabaseInfo.CAR_MODEL;
import static com.example.automechapp.database.DatabaseInfo.EDIT_TIME;
import static com.example.automechapp.database.DatabaseInfo.STANDARD_COMMENT;
import static com.example.automechapp.database.DatabaseInfo.STANDARD_DATE;
import static com.example.automechapp.database.DatabaseInfo.STANDARD_DESCRIPTION;
import static com.example.automechapp.database.DatabaseInfo.STANDARD_ID;
import static com.example.automechapp.database.DatabaseInfo.STANDARD_PHOTO;
import static com.example.automechapp.database.DatabaseInfo.WORK_PRICE;
import static com.example.automechapp.database.DatabaseInfo.WORK_TIME;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.example.automechapp.breakdown.Breakdown;
import com.example.automechapp.camera_utils.ImageUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

public class GetBreakdowns extends Thread {
    String table;
    DatabaseInterface database;
    String[] projection;
    String selection;
    String sortOrder;
    Context context;
    int id = -1;

    ArrayList<Breakdown> data = new ArrayList<>();

    private GetBreakdowns(DatabaseInterface database, String selection, String sortOrder){
        this.database = database;
        this.selection = selection;
        this.sortOrder = sortOrder;

        this.table = BREAKDOWNS_TABLE;
    }

    public GetBreakdowns(Context context, String selection, String sortOrder) {
        this(new DatabaseInterface(context), selection, sortOrder);
        this.context = context;
    }

    public GetBreakdowns(Context context, String selection, String sortOrder, int id) {
        this(context, selection, sortOrder);
        this.id = id;
    }


    @Override
    public void run(){
        if (id > -1) {
            projection = new String[] {
                    STANDARD_ID,
                    CAR_ID,
                    BREAKDOWN_NAME,
                    BREAKDOWN_PHOTO,
                    STANDARD_DATE,
                    EDIT_TIME,
                    WORK_TIME,
                    WORK_PRICE,
                    STANDARD_COMMENT,
                    STANDARD_DESCRIPTION,
                    BREAKDOWN_TYPE,
                    BREAKDOWN_STATE
            };
        }
        else {
            projection = new String[] {
                    STANDARD_ID,
                    CAR_ID,
                    BREAKDOWN_NAME,
                    BREAKDOWN_PHOTO,
                    STANDARD_DATE,
                    EDIT_TIME
            };
        }

        GetData getData = database.getData(DatabaseInfo.BREAKDOWNS_TABLE, projection, selection, sortOrder);

        try {
            getData.join();
            Cursor cursor = getData.getCursor();


            if (cursor == null) {
                new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(context, "ошибка", Toast.LENGTH_SHORT).show());
                return;
            }

            int[] indexes = new int[] {
                    cursor.getColumnIndex(BREAKDOWN_ID),
                    cursor.getColumnIndex(CAR_ID),
                    cursor.getColumnIndex(BREAKDOWN_NAME),
                    cursor.getColumnIndex(BREAKDOWN_PHOTO),
                    cursor.getColumnIndex(STANDARD_DATE),
                    cursor.getColumnIndex(EDIT_TIME),
                    cursor.getColumnIndex(WORK_TIME),
                    cursor.getColumnIndex(WORK_PRICE),
                    cursor.getColumnIndex(STANDARD_COMMENT),
                    cursor.getColumnIndex(STANDARD_DESCRIPTION),
                    cursor.getColumnIndex(BREAKDOWN_TYPE),
                    cursor.getColumnIndex(BREAKDOWN_STATE)

            };

            if (id > -1) {
                GetData getData1 = database.getData(
                        BREAKDOWNS_PHOTOS_TABLE,
                        new String[] {
                                STANDARD_ID,
                                STANDARD_PHOTO
                        },
                        BREAKDOWN_ID + " = '" + id + "'",
                        STANDARD_DATE + " ASC"
                );

                Cursor photos_cursor;
                ArrayList<Bitmap> photos = new ArrayList<Bitmap>();

                try {
                    getData1.join();
                    photos_cursor = getData1.getCursor();
                    int photoIndex = photos_cursor.getColumnIndex(STANDARD_PHOTO);

                    while (photos_cursor.moveToNext()) {
                        photos.add(ImageUtil.getByteArrayAsBitmap(
                                photos_cursor.getBlob(photoIndex)
                        ));
                    }
                }
                catch (Exception e) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        e.printStackTrace();
                        Toast.makeText(context, "Не удалось загрузить фото", Toast.LENGTH_SHORT).show();
                    });
                    return;
                }

                while (cursor.moveToNext()) {
                    data.add(new Breakdown(
                            cursor.getInt(indexes[0]),
                            cursor.getInt(indexes[1]),
                            cursor.getString(indexes[2]),
                            ImageUtil.getByteArrayAsBitmap(cursor.getBlob(indexes[3])),
                            cursor.getString(indexes[4]),
                            cursor.getString(indexes[5]),
                            cursor.getString(indexes[6]),
                            cursor.getInt(indexes[7]),
                            cursor.getString(indexes[8]),
                            cursor.getString(indexes[9]),
                            cursor.getInt(indexes[10]),
                            cursor.getInt(indexes[11]),
                            photos
                    ));
                }
            }
            else {
                HashSet<String> ids = new HashSet<>();

                while (cursor.moveToNext()) {
                    data.add(new Breakdown(
                            cursor.getInt(indexes[0]),
                            cursor.getInt(indexes[1]),
                            cursor.getString(indexes[2]),
                            ImageUtil.getByteArrayAsBitmap(cursor.getBlob(indexes[3])),
                            cursor.getString(indexes[4]),
                            cursor.getString(indexes[5]),
                            "",
                            ""
                    ));

                    ids.add(cursor.getString(indexes[1]));
                }

                if (ids.size() > 0) {
                    // Закрываем курсор, которым мы тянули поломки.
                    cursor.close();

                    String selection = CAR_ID + " in (" + String.join(", ", ids) + ")";
                    GetData getCars = database.getData(
                            CARS_TABLE,
                            new String[] {
                                CAR_ID,
                                CAR_MANUFACTURE,
                                CAR_MODEL
                            },
                            selection,
                            CAR_ID + " DESC"
                    );

                    getCars.join();

                    cursor = getCars.getCursor();

                    if (cursor == null) {
                        new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(context, "ошибка при дозагрузке авто", Toast.LENGTH_SHORT).show());
                        return;
                    }

                    HashMap<Integer, String[]> cars = new HashMap<>();
                    int[] car_indexes = new int[] {
                        cursor.getColumnIndex(CAR_ID),
                        cursor.getColumnIndex(CAR_MANUFACTURE),
                        cursor.getColumnIndex(CAR_MODEL)
                    };

                    while (cursor.moveToNext()) {
                        cars.put(
                            cursor.getInt(car_indexes[0]),
                            new String[]{
                                cursor.getString(car_indexes[1]),
                                cursor.getString(car_indexes[2])
                            }
                        );
                    }

                    if (cars.size() != 0) {
                        for (Breakdown breakdown : data) {
                            int id = breakdown.getCar_id();

                            breakdown.setManufacture(Objects.requireNonNull(cars.get(id))[0]);
                            breakdown.setModel(Objects.requireNonNull(cars.get(id))[1]);
                        }
                    }

                }
            }
            cursor.close();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Breakdown> getData() {
        return data;
    }
}
