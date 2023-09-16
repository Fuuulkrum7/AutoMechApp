package com.example.automechapp.camera_utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.automechapp.R;
import com.example.automechapp.ViewPagerAdapter;
import com.google.android.material.appbar.AppBarLayout;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

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

    protected MenuItem delete_data_item;
    protected MenuItem edit_data_item;
    protected MenuItem add_photo_item;
    protected int nightModeFlags;
    protected boolean isEditable = true;
    protected boolean edit = false;
    AppBarStateChangeListener.State prev_state;
    protected AppBarStateChangeListener listener = new AppBarStateChangeListener() {
        @Override
        public void onStateChanged(AppBarLayout appBarLayout, AppBarStateChangeListener.State
        state) {
            prev_state = state;

            if (state == AppBarStateChangeListener.State.EXPANDED)
                return;

            for (MenuItem item : new MenuItem[] {add_photo_item, delete_data_item, edit_data_item}) {
                Drawable drawable = item.getIcon();
                float alpha = (item == add_photo_item) == isEditable ? 1 : 0.5f;
                if (nightModeFlags == Configuration.UI_MODE_NIGHT_NO && state == AppBarStateChangeListener.State.COLLAPSED) {
                    drawable.mutate();
                    drawable.setColorFilter(adjustAlpha(Color.BLACK, alpha), PorterDuff.Mode.SRC_ATOP);
                }
                else {
                    drawable.mutate();
                    drawable.setColorFilter(adjustAlpha(alpha == 0.5f ? Color.GRAY : Color.WHITE, alpha), PorterDuff.Mode.SRC_ATOP);
                }
            }

            if (nightModeFlags == Configuration.UI_MODE_NIGHT_NO && state == AppBarStateChangeListener.State.COLLAPSED) {
                Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.arrow_back_black);
            }
            else {
                Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.arrow_back);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        nightModeFlags = getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;
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
            case R.id.delete_data:
                if (id > 0) {
                    deleteData();
                }
                break;
            case R.id.edit_data:
                if (id > 0) {
                    updateData();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void deleteData() {

    }

    protected void updateData() {
        changeState(true);
        edit = true;
    }

    @ColorInt
    public static int adjustAlpha(@ColorInt int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

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

    private void updateColors() {
        for (MenuItem item : new MenuItem[] {add_photo_item, delete_data_item, edit_data_item}) {
            Drawable drawable = item.getIcon();

            float alpha = (item == add_photo_item) == isEditable ? 1 : 0.5f;
            if (nightModeFlags == Configuration.UI_MODE_NIGHT_NO) {
                drawable.mutate();
                drawable.setColorFilter(adjustAlpha(Color.BLACK, alpha), PorterDuff.Mode.SRC_ATOP);
            }
            else {
                drawable.mutate();
                drawable.setColorFilter(adjustAlpha(alpha == 0.5f ? Color.GRAY : Color.WHITE, alpha), PorterDuff.Mode.SRC_ATOP);
            }
        }

        if (nightModeFlags == Configuration.UI_MODE_NIGHT_NO) {
            Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.arrow_back_black);
        }
        else {
            Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.arrow_back);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        delete_data_item = menu.findItem(R.id.delete_data);
        edit_data_item = menu.findItem(R.id.edit_data);
        add_photo_item = menu.findItem(R.id.add_photo);

        delete_data_item.setEnabled(!isEditable);
        edit_data_item.setEnabled(!isEditable);
        add_photo_item.setEnabled(isEditable);

        updateColors();

        return true;
    }

    protected void changeState(boolean state) {
        isEditable = state;

        if (delete_data_item != null) {
            delete_data_item.setEnabled(!state);
            edit_data_item.setEnabled(!state);
            add_photo_item.setEnabled(state);

            updateColors();
        }
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

                            Bitmap bitmap = ImageUtil.getUriAsBitmap(uri, this);
                            int koef = Math.max(bitmap.getWidth(), bitmap.getHeight()) / 720;
                            if (koef > 1) {
                                bitmap = ImageUtil.getScaledBitmap(
                                        bitmap,
                                        bitmap.getWidth() / koef,
                                        bitmap.getHeight() / koef
                                );
                            }
                            bitmaps.add(bitmap);
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
            int koef = Math.max(bitmap.getWidth(), bitmap.getHeight()) / 720;
            if (koef > 1) {
                bitmap = ImageUtil.getScaledBitmap(
                        bitmap,
                        bitmap.getWidth() / koef,
                        bitmap.getHeight() / koef
                );
            }
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
