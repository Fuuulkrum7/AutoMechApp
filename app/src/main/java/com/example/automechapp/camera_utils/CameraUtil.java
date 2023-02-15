package com.example.automechapp.camera_utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

// Утилита для работы с камерой/галереей
public class CameraUtil {
    // Путь до созданного фото в файловой системе
    private String currentPhotoPath;
    // Соответственно, контекст
    private final Context context;

    public CameraUtil(Context context){
        this.context = context;
    }

    // Создаем интент для запуска камеры
    public Intent createCameraIntent() {
        // Создаем интент
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Файл под фото
        File photoFile = null;
        // Создаем его
        try {
            photoFile = createImageFile();
        }
        // В слчае ошибки
        catch (IOException e) {
            e.printStackTrace();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    e.printStackTrace();
                    Toast.makeText(context, "Error hapened", Toast.LENGTH_SHORT).show();
                }
            });
        }
        // Если все хорошо
        if (photoFile != null) {
            // Куда класть фото по окончании работы
            Uri photoURI = FileProvider.getUriForFile(context,
                    "com.example.android.fileprovider",
                    photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            // Возвращаем интент
            return takePictureIntent;
        }

        return null;
    }

    // создание файла
    private File createImageFile() throws IOException {
        // Само создание. Получаем уникальное имя с помощью времени
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* префикс */
                ".jpg",         /* суффикс */
                storageDir      /* директория */
        );

        // Получаем абсолютный путь
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    // Получаем путь
    public String getCurrentPhotoPath() {
        return currentPhotoPath;
    }

    // задаем путь
    public void setCurrentPhotoPath(String currentPhotoPath) {
        this.currentPhotoPath = currentPhotoPath;
    }

    // Создание интента для галереи
    public Intent createGalleryIntent(int code) {
        Intent intent = new Intent();
        intent.setType("image/*");
        if (code == PhotosAdder.ICON_CODE) {
            // Если нужна одна фотка для иконки
            intent.setAction(Intent.ACTION_PICK);
            return intent;
        }
        // Если много
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        return Intent.createChooser(intent,"Select Picture");
    }
}
