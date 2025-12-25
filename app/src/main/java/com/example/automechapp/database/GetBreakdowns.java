package com.example.automechapp.database;

import static com.example.automechapp.database.DatabaseInfo.*;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.automechapp.MainActivity;
import com.example.automechapp.breakdown.Breakdown;
import com.example.automechapp.camera_utils.ImageUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

public class GetBreakdowns extends GetData<Breakdown> {

    private final DatabaseInterface database;
    private final int id; // breakdown_id (если > -1 -> детальный режим)

    public GetBreakdowns(Context context, String selection, String sortOrder) {
        this(context, selection, sortOrder, -1);
    }

    public GetBreakdowns(Context context, String selection, String sortOrder, int id) {
        super(
                context,
                BREAKDOWNS_TABLE,
                buildBreakdownProjection(id > -1),
                selection,
                sortOrder
        );
        this.database = new DatabaseInterface(context);
        this.id = id;
    }

    private static String[] buildBreakdownProjection(boolean full) {
        if (full) {
            return new String[]{
                    BREAKDOWN_ID,
                    CAR_ID,
                    BREAKDOWN_NAME,
                    BREAKDOWN_PHOTO,
                    STANDARD_DATE,
                    EDIT_TIME,
                    WORK_PRICE,
                    STANDARD_COMMENT,
                    STANDARD_DESCRIPTION,
                    BREAKDOWN_TYPE,
                    BREAKDOWN_STATE
            };
        }
        return new String[]{
                BREAKDOWN_ID,
                CAR_ID,
                BREAKDOWN_NAME,
                BREAKDOWN_PHOTO,
                STANDARD_DATE,
                EDIT_TIME
        };
    }

    @Override
    public void run() {
        database.getData(this);

        Cursor cursor = null;
        try {
            cursor = getCursor();
            if (cursor == null) {
                toastOnUi("ошибка");
                return;
            }

            // индексы
            final int idxBreakdownId = cursor.getColumnIndex(BREAKDOWN_ID);
            final int idxCarId = cursor.getColumnIndex(CAR_ID);
            final int idxName = cursor.getColumnIndex(BREAKDOWN_NAME);
            final int idxPhoto = cursor.getColumnIndex(BREAKDOWN_PHOTO);
            final int idxDate = cursor.getColumnIndex(STANDARD_DATE);
            final int idxEditTime = cursor.getColumnIndex(EDIT_TIME);

            final int idxWorkPrice = cursor.getColumnIndex(WORK_PRICE);
            final int idxComment = cursor.getColumnIndex(STANDARD_COMMENT);
            final int idxDesc = cursor.getColumnIndex(STANDARD_DESCRIPTION);
            final int idxType = cursor.getColumnIndex(BREAKDOWN_TYPE);
            final int idxState = cursor.getColumnIndex(BREAKDOWN_STATE);

            // ===== Детальный режим: одна поломка + фото =====
            if (id > -1) {
                // Сначала читаем строку(и) из breakdowns
                while (cursor.moveToNext()) {
                    // ФОТО через общий класс (как в GetCars)
                    GetAnyPhotos task = new GetAnyPhotos(context, id, BREAKDOWNS_PHOTOS_TABLE, BREAKDOWN_ID);
                    task.run();
                    ArrayList<Bitmap> photos = task.getPhotos();

                    data.add(new Breakdown(
                            cursor.getInt(idxBreakdownId),
                            cursor.getInt(idxCarId),
                            cursor.getString(idxName),
                            ImageUtil.getByteArrayAsBitmap(cursor.getBlob(idxPhoto)),
                            cursor.getString(idxDate),
                            cursor.getString(idxEditTime),
                            cursor.getInt(idxWorkPrice),
                            cursor.getString(idxComment),
                            cursor.getString(idxDesc),
                            cursor.getInt(idxType),
                            cursor.getInt(idxState),
                            photos
                    ));
                }
                onFinish();
                return;
            }

            // ===== Списочный режим: поломки + дозагрузка марки/модели авто =====
            HashSet<Integer> carIds = new HashSet<>();

            while (cursor.moveToNext()) {
                int carId = cursor.getInt(idxCarId);

                data.add(new Breakdown(
                        cursor.getInt(idxBreakdownId),
                        carId,
                        cursor.getString(idxName),
                        ImageUtil.getByteArrayAsBitmap(cursor.getBlob(idxPhoto)),
                        cursor.getString(idxDate),
                        cursor.getString(idxEditTime),
                        "",
                        ""
                ));

                if (carId > 0) carIds.add(carId);
            }

            // закрываем cursor с поломками до второго запроса
            cursor.close();
            cursor = null;

            if (!carIds.isEmpty()) {
                HashMap<Integer, String[]> carsMap = loadCarsManufactureModel(carIds);

                if (!carsMap.isEmpty()) {
                    for (Breakdown b : data) {
                        int carId = b.getCar_id();
                        if (carId > 0) {
                            try {
                                b.setManufacture(Objects.requireNonNull(carsMap.get(carId))[0]);
                                b.setModel(Objects.requireNonNull(carsMap.get(carId))[1]);
                            } catch (Exception e) {
                                Log.d(MainActivity.TAG, e.toString());
                            }
                        }
                    }
                }
            }
            onFinish();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            close();
        }
    }

    private HashMap<Integer, String[]> loadCarsManufactureModel(HashSet<Integer> ids) {
        HashMap<Integer, String[]> cars = new HashMap<>();

        GetData<Void> getCars = new GetData<>(
                context,
                CARS_TABLE,
                new String[]{CAR_ID, CAR_MANUFACTURE, CAR_MODEL},
                CAR_ID + " in (" + joinInts(ids) + ")",
                CAR_ID + " DESC"
        );

        database.getData(getCars);

        try (Cursor c = getCars.getCursor()) {
            if (c == null) {
                toastOnUi("ошибка при дозагрузке авто");
                return cars;
            }

            int idxId = c.getColumnIndex(CAR_ID);
            int idxMan = c.getColumnIndex(CAR_MANUFACTURE);
            int idxModel = c.getColumnIndex(CAR_MODEL);

            while (c.moveToNext()) {
                cars.put(
                        c.getInt(idxId),
                        new String[]{c.getString(idxMan), c.getString(idxModel)}
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            getCars.close();
        }

        return cars;
    }

    private static String joinInts(HashSet<Integer> ids) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Integer id : ids) {
            if (id == null) continue;
            if (!first) sb.append(", ");
            sb.append(id);
            first = false;
        }
        return sb.toString();
    }

    private void toastOnUi(String msg) {
        new Handler(Looper.getMainLooper()).post(
                () -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        );
    }
}
