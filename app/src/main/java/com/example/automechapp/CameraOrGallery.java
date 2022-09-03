package com.example.automechapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

// Диалог для выбора, откуда загружать изображение
public class CameraOrGallery extends DialogFragment {
    // Что будем запускать для получения данных
    private final PhotosAdder photosAdder;
    int code;

    // Инициализация
    CameraOrGallery(PhotosAdder photosAdder, int code) {
        this.photosAdder = photosAdder;
        this.code = code;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Текст для диалога
        String title = "Выбор есть всегда";
        String message = "Загрузить фото из галерии и сфоткать сейчас?";
        String button1String = "Галерея";
        String button2String = "Камера";

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // заголовок
        builder.setTitle(title);
        // сообщение
        builder.setMessage(message);

        // Ставим прослушку
        builder.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                photosAdder.openGallery(code);
            }
        });
        builder.setNegativeButton(button2String, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                photosAdder.startCamera(code);
            }
        });

        builder.setCancelable(true);

        return builder.create();
    }
}
