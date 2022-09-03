package com.example.automechapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class CameraOrGallery extends DialogFragment {
    private final CarActivity carActivity;
    int code;

    CameraOrGallery(CarActivity carActivity, int code) {
        this.carActivity = carActivity;
        this.code = code;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = "Выбор есть всегда";
        String message = "Загрузить фото из галерии и сфоткать сейчас?";
        String button1String = "Галерея";
        String button2String = "Камера";

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);  // заголовок
        builder.setMessage(message); // сообщение
        builder.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                carActivity.openGallery(code);
            }
        });
        builder.setNegativeButton(button2String, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                carActivity.startCamera(code);
            }
        });
        builder.setCancelable(true);

        return builder.create();
    }
}
