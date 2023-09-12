package com.example.automechapp.camera_utils;

import static com.example.automechapp.camera_utils.PhotosAdder.COEFFICIENT;
import static com.example.automechapp.camera_utils.PhotosAdder.ICON_CODE;
import static com.example.automechapp.camera_utils.PhotosAdder.PHOTO_CODE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.automechapp.R;
import com.example.automechapp.ViewPagerAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import id.zelory.compressor.Compressor;

public abstract class PhotoWorker extends AppCompatActivity implements PhotosAdder {
    // Адаптер, массив фоток и путь до сделанной фотки
    protected ViewPagerAdapter viewPagerAdapter;
    public ArrayList<Bitmap> bitmaps = new ArrayList<>();
    protected String currentPhotoPath;

    @SuppressLint("StaticFieldLeak")
    protected static Context context;
    protected ImageView icon;

    // Тут храним изображение и длину введенного года, это защита от дурака
    protected ViewPager2 imageSwitcher;
    // уникальный id поломки для обращения к бд
    protected int id = -1;

    // Показываем диалоговое окно для выбора, откуда брать изображение - камера или галерея
    protected void getUserImage(int code) {
        CameraOrGallery choose = new CameraOrGallery(this, code);
        FragmentManager manager = getSupportFragmentManager();
        choose.show(manager, "dialog");
    }

    // Запуск камеры
    @SuppressLint("QueryPermissionsNeeded")
    public void startCamera(int code) {
        // Получаем утилиту для получения интента камеры и пути для фото
        CameraUtil cameraUtil = new CameraUtil(this);
        Intent intent = cameraUtil.createCameraIntent();
        currentPhotoPath = cameraUtil.getCurrentPhotoPath();
        // Запускаем интент
        startActivityForResult(intent, code);
    }

    // Открываем галерею
    @SuppressLint("QueryPermissionsNeeded")
    public void openGallery(int code) {
        // Получаем интент и тд, умножаем код на коэффициент
        CameraUtil cameraUtil = new CameraUtil(this);
        Intent intent = cameraUtil.createGalleryIntent(code);
        startActivityForResult(intent, code * COEFFICIENT);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        // Меняем меню
        getMenuInflater().inflate(R.menu.add_photo_menu, menu);
        return true;
    }

    // Обработка нажатия на кнопку в меню
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_photo:
                if (id < 0)
                    getUserImage(PHOTO_CODE);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Метод обработки получения фото
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Если все хорошо
        if (resultCode == RESULT_OK) {
            try {
                // Если код меньше 10, т.е. меньше коэффициента и фотка получено с камеры
                if (requestCode < 10) {
                    // Добавляем фотку и удаляем файл
                    removeImageFile(requestCode);
                }
                // Если же нет, то бишь фотка из галереи
                else {
                    // Если фоток больше, чем одна, и коэффициент - не код иконки
                    // то бишь фотка нужна не одна
                    if (data.getClipData() != null && requestCode != ICON_CODE * COEFFICIENT) {
                        // Получаем число фото
                        int count = data.getClipData().getItemCount();

                        // Перебираем фотографии и добавляем их в список фотографий
                        for (int i = 0; i < count; i++) {
                            Uri uri = data.getClipData().getItemAt(i).getUri();
                            bitmaps.add(ImageUtil.getUriAsBitmap(uri, this));
                        }

                        // Обновляем фото в pageViewer2
                        setImages();
                    }
                    else {
                        // Добавлем одно (нужное) фото
                        Uri photo = data.getData();
                        // Меняем значение, так как здесь надо только добавить иображение (одно)
                        requestCode /= COEFFICIENT;
                        // Добавляем изображение
                        setImage(photo, requestCode);
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Произошла ошибка", Toast.LENGTH_SHORT).show();
            }
        }
    }


    // Получаем контекст
    public static Context getContext() {
        return context;
    }

    // Получаем id
    public int getId() {
        return id;
    }

    // Ставим id
    public void setId(int id) {
        this.id = id;
    }

    // Удаляем файл и пересохраняем фото
    protected void removeImageFile(int code) throws IOException {
        try {
            // Сжимаем фото
            File f = new Compressor(this)
                    .setMaxWidth(code == ICON_CODE ? 128 : 640)
                    .setMaxHeight(code == ICON_CODE ? 128 : 480)
                    .setQuality(50)
                    .compressToFile(new File(currentPhotoPath));

            // Получаем из него данные
            Uri contentUri = Uri.fromFile(f);

            // Ставим изображение, куда нужно
            setImage(contentUri, code);
            // Удаляем фото в памяти
            f.delete();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
        }
    }

    // Ставим изображение
    protected void setImage(Uri contentUri, int code) throws IOException {
        Bitmap bitmap = ImageUtil.getUriAsBitmap(contentUri, this);
        // Если надо поставить изображение в imageview для иконки
        if (code == ICON_CODE || code == ICON_CODE * COEFFICIENT) {
            bitmap = ImageUtil.getScaledBitmap(
                    ImageUtil.getSquaredBitmap(bitmap),
                    128,
                    128
            );

            icon.setImageBitmap(bitmap);
        }
        // Если фото нужно для viewpager (одно фото)
        else if (code == PHOTO_CODE) {
            // TODO убрать костыль
            bitmaps.add(bitmap);
            setImages();
        }
        // Если много фото
        else if (code == PHOTO_CODE * COEFFICIENT) {
            setImages();
        }
    }

    // Обновляем фото в viewpager
    protected void setImages() {
        viewPagerAdapter = new ViewPagerAdapter(this, bitmaps);
        imageSwitcher.setAdapter(viewPagerAdapter);
    }
}
