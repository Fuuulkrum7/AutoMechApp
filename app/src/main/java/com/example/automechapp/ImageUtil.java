package com.example.automechapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

public final class ImageUtil {
    ImageUtil() {}

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

    public static Bitmap getByteArrayAsBitmap(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}
