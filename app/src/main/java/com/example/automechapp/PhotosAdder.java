package com.example.automechapp;

// Интерфейс для запуска камеры/галереи. Нужен для работы диалога с выбором камеры или галереи
public interface PhotosAdder {
    // Коды для получения изображения. Для определения, куда потом изображение пихать.
    // Умножаем на коэф., если выбор из галереи
    public static final int ICON_CODE = 1;
    public static final int PHOTO_CODE = 2;
    public static final int COEFFICIENT = 10;
    default void openGallery(int code) {}

    default void startCamera(int code) {}
}
