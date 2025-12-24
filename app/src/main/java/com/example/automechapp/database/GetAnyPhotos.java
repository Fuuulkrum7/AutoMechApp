package com.example.automechapp.database;

import static com.example.automechapp.database.DatabaseInfo.*;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.example.automechapp.camera_utils.ImageUtil;

import java.util.ArrayList;

public class GetAnyPhotos extends GetData<Bitmap> {

    public GetAnyPhotos(Context context, int id, String table, String idName) {
        super(
                context,
                table,
                new String[]{
                        STANDARD_ID,
                        STANDARD_PHOTO
                },
                idName + " = '" + id + "'",
                STANDARD_DATE + " ASC"
        );
    }

    @Override
    public void run() {
        DatabaseInterface databaseInterface = new DatabaseInterface(context);
        databaseInterface.getData(this);

        try (Cursor cursor = getCursor()) {
            if (cursor == null) {
                throw new IllegalStateException("Cursor is null (query failed)");
            }

            int photoIndex = cursor.getColumnIndex(STANDARD_PHOTO);
            if (photoIndex < 0) {
                throw new IllegalStateException("Column not found: " + STANDARD_PHOTO);
            }

            while (cursor.moveToNext()) {
                byte[] blob = cursor.getBlob(photoIndex);
                if (blob != null) {
                    data.add(ImageUtil.getByteArrayAsBitmap(blob));
                }
            }
        } catch (Exception e) {
            // Ошибка -> показать toast на UI потоке
            new Handler(Looper.getMainLooper()).post(() -> {
                e.printStackTrace();
                Toast.makeText(context, "Не удалось загрузить фото", Toast.LENGTH_SHORT).show();
            });
        } finally {
            close(); // закрыть db из GetData
        }
    }

    public ArrayList<Bitmap> getPhotos() {
        return data;
    }
}
