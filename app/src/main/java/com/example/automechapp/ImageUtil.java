package com.example.automechapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

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

    public static Bitmap getScaledBitmap (Bitmap bitmap, int width, int height) {
        Bitmap result = Bitmap.createScaledBitmap(bitmap, width, height, true);
        bitmap.recycle();

        return result;
    }

    public static Bitmap getSquaredBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Bitmap result = null;

        if (height > width) {
            result = Bitmap.createBitmap(bitmap, 0, height / 2 - width / 2,
                    width, width);
        } else {
            result = Bitmap.createBitmap(bitmap, width / 2 - height / 2, 0,
                    height, height);
        }

        bitmap.recycle();

        return result;
    }

    public static Bitmap getUriAsBitmap(Uri uri, Context context) throws IOException {
        return MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
    }
}
