package com.example.automechapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import id.zelory.compressor.Compressor;

public class CarActivity extends AppCompatActivity implements PhotosAdder{
    // уникальный id машины для обращения к бд
    private int id = -1;

    // Тут просто один коммент, это перменные для хранения всех полей текстовых
    EditText year;
    ImageView icon;
    Button getTextData;
    Button saveData;

    EditText manufacture;
    EditText name;
    EditText model;
    EditText price;
    EditText color;
    EditText vin;
    EditText engine_volume;
    EditText engine_model;
    EditText engine_number;
    EditText car_state_number;
    EditText tax;
    EditText horsepower;

    Spinner ownersSpinner;
    boolean allowed = false;

    // А это тоже id. Но пользователя, получаем его при добавлении, так тупо легче это все оформить.
    int user_id = 0;

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    // Тут храним изображение и длину введенного года, это защита от дурака
    ViewPager2 imageSwitcher;
    int length_of_year = 0;

    // Сам год числом
    int car_year = 0;
    // Адаптер, массив фоток и путь до сделанной фотки
    ViewPagerAdapter viewPagerAdapter;
    ArrayList<Bitmap> bitmaps = new ArrayList<>();
    String currentPhotoPath;

    ArrayList<Owner> owners;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car);

        // тулбар, без него никуда
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Автомобиль");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // сохраняем на всякий контекст
        context = this;

        // Кнопка сохранения данных
        saveData = (Button) findViewById(R.id.save_button);
        saveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDataSave();
            }
        });

        // Все поля ввода данных
        name = (EditText) findViewById(R.id.name);
        manufacture = (EditText) findViewById(R.id.manufacture);
        model = (EditText) findViewById(R.id.model);
        price = (EditText) findViewById(R.id.price);
        color = (EditText) findViewById(R.id.color);
        vin = (EditText) findViewById(R.id.vin);
        engine_volume = (EditText) findViewById(R.id.engine_volume);
        engine_model = (EditText) findViewById(R.id.engine_model);
        engine_number = (EditText) findViewById(R.id.engine_number);
        car_state_number = (EditText) findViewById(R.id.car_state_number);
        tax = (EditText) findViewById(R.id.tax);
        horsepower = (EditText) findViewById(R.id.horsepower);

        CarStateNumberListener listener = new CarStateNumberListener(car_state_number);
        car_state_number.addTextChangedListener(listener);
        car_state_number.setFilters(new InputFilter[] { listener.getFilter() });

        year = findViewById(R.id.year);
        year.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            // Проверка корректности введенного года
            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    length_of_year = year.length();
                    car_year = Integer.parseInt(year.getText().toString());
                }
                catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                // первое авто было в 1886, значит, до него ничего быть не может
                if (length_of_year == 4 && car_year < 1886) {
                    Toast.makeText(CarActivity.this, "Некорректный год", Toast.LENGTH_SHORT).show();
                }
                else if (length_of_year == 4) {
                    allowed = true;
                }

                if (length_of_year < 4) {
                    allowed = false;
                }
            }
        });

        // Иконка
        icon = findViewById(R.id.breakdown_icon);
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // id меньше 0 тогда, когда авто только создается, т.е. фото делать надо
                if (id < 0)
                    getUserImage(ICON_CODE);
            }
        });

        // Переход на сайт для рассчета налога
        getTextData = findViewById(R.id.count_tax);
        getTextData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO изменить на ручной рассчет
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.nalog.gov.ru/rn74/service/calc_transport/"));
                startActivity(browserIntent);
            }
        });

        // viewpager2 для перелистывания картинок в шапке
        imageSwitcher = findViewById(R.id.image_switcher);
        // Ставим адаптер
        viewPagerAdapter = new ViewPagerAdapter(CarActivity.this, bitmaps);
        imageSwitcher.setAdapter(viewPagerAdapter);

        // И прослушку
        imageSwitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        Bundle bundle = getIntent().getExtras();

        // получаеа данные, если есть id, то загружаем по нему авто
        if (bundle != null) {
            id = bundle.getInt("id");
            user_id = bundle.getInt("user_id");

            saveData.setVisibility(View.INVISIBLE);
            GetCurrentCar getCurrentCar = new GetCurrentCar(this, id);
            getCurrentCar.start();
            disableText();
        }

        ownersSpinner = findViewById(R.id.spinner);
        ownersSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                user_id = owners.get(i).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        getOwnersList();
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences settings = getSharedPreferences(MainActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        editor.putInt(
                MainActivity.APP_PREFERENCES_CODE,
                R.id.nav_cars
        );
        editor.apply();
    }

    public void getOwnersList() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                GetOwners getOwners = new GetOwners(getContext(), "");
                getOwners.start();

                try {
                    getOwners.join();
                    owners = getOwners.getOwnersList();

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            ownersSpinner.setAdapter(new OwnerSpinnerAdapter(getContext(), R.layout.spinner_dropdown_item, owners));
                        }
                    });

                    user_id = owners.get(0).getId();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();
    }

    // Запуск сохранени данных
    private void startDataSave() {
        ContentValues contentValues = getValues();
        if (contentValues == null) {
            Toast.makeText(this, "Введите данные", Toast.LENGTH_SHORT).show();
            return;
        }
        // Создаем и запускаем поток для сохранения данных
        SaveCar save = new SaveCar(this, getContext(), contentValues);
        save.start();
        // Прячем кнопку
        saveData.setVisibility(View.INVISIBLE);
        // Убираем возможность для пользователя редактировать текст
        disableText();
    }

    private void disableText() {
        // Ставим имя как заголовок
        getSupportActionBar().setTitle(name.getText().toString());

        // Вырубаем текст
        manufacture.setEnabled(false);
        model.setEnabled(false);
        year.setEnabled(false);
        car_state_number.setEnabled(false);
        name.setEnabled(false);
        vin.setEnabled(false);
        engine_number.setEnabled(false);
        engine_model.setEnabled(false);
        engine_volume.setEnabled(false);
        tax.setEnabled(false);
        price.setEnabled(false);
        color.setEnabled(false);
        horsepower.setEnabled(false);
    }

    // Показываем диалоговое окно для выбора, откуда брать изображение - камера или галерея
    private void getUserImage(int code) {
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

    // Парсим данные из экземпляра машины
    @SuppressLint("SetTextI18n")
    public void setData(Car car) {
        name.setText(car.getCarName());
        manufacture.setText(car.getManufacture());
        model.setText(car.getModel());
        year.setText(Integer.toString(car.getCar_year()));
        price.setText(Integer.toString(car.getCar_price()));
        color.setText(car.getCar_color());
        vin.setText(car.getVIN());
        engine_volume.setText(Float.toString(car.getEngine_volume()));
        engine_model.setText(car.getEngine_model());
        engine_number.setText(car.getEngine_number());
        car_state_number.setText(car.getCar_state_number());
        tax.setText(Integer.toString(car.getTax()));
        horsepower.setText(Integer.toString(car.getHorsepower()));
        icon.setImageBitmap(car.getIcon());

        user_id = car.getUser_id();

        getSupportActionBar().setTitle(car.getCarName());

        bitmaps = car.getCar_photos();
        setImages();

        Thread thread = new Thread() {
            @Override
            public void run() {
                GetOwners getOwners = new GetOwners(getContext(), DatabaseInfo.OWNER_ID + " = " + user_id);
                getOwners.start();

                try {
                    getOwners.join();
                    owners = getOwners.getOwnersList();

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            ownersSpinner.setAdapter(new OwnerSpinnerAdapter(getContext(), R.layout.spinner_dropdown_item, owners));
                        }
                    });
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();
    }

    // Парсим данные из текстовых перменных
    public ContentValues getValues() {
        if (!allowed)
            return null;

        ContentValues contentValues = new ContentValues();

        try {
            contentValues.put(DatabaseInfo.CAR_NAME, name.getText().toString());
            contentValues.put(DatabaseInfo.CAR_MANUFACTURE, manufacture.getText().toString());
            contentValues.put(DatabaseInfo.CAR_MODEL, model.getText().toString());
            contentValues.put(DatabaseInfo.CAR_YEAR, Integer.parseInt(year.getText().toString()));
            contentValues.put(DatabaseInfo.CAR_PRICE, Long.parseLong(price.getText().toString()));
            contentValues.put(DatabaseInfo.COLOR, color.getText().toString());
            contentValues.put(DatabaseInfo.VIN, vin.getText().toString());
            contentValues.put(DatabaseInfo.ENGINE_VOLUME, Float.parseFloat(engine_volume.getText().toString()));
            contentValues.put(DatabaseInfo.ENGINE_MODEL, engine_model.getText().toString());
            contentValues.put(DatabaseInfo.ENGINE_NUMBER, engine_number.getText().toString());
            contentValues.put(DatabaseInfo.STATE_CAR_NUMBER, car_state_number.getText().toString());
            contentValues.put(DatabaseInfo.TAX, Integer.parseInt(tax.getText().toString()));
            contentValues.put(DatabaseInfo.CAR_PHOTO, ImageUtil.getBitmapAsByteArray((((BitmapDrawable) icon.getDrawable()).getBitmap())));
            contentValues.put(DatabaseInfo.HORSEPOWER, Integer.parseInt(horsepower.getText().toString()));
            contentValues.put(DatabaseInfo.OWNER_ID, user_id);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        String date = sdf.format(c.getTime());

        contentValues.put(DatabaseInfo.STANDARD_DATE, date);

        for (String s: contentValues.keySet()) {
            if (contentValues.get(s) == null) {
                return null;
            }
        }

        if (bitmaps.size() == 0) {
            return null;
        }

        return contentValues;
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

    // Получаем контекст
    static Context getContext() {
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
    private void removeImageFile(int code) throws IOException {
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
    private void setImage(Uri contentUri, int code) throws IOException {
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
    private void setImages() {
        viewPagerAdapter = new ViewPagerAdapter(this, bitmaps);
        imageSwitcher.setAdapter(viewPagerAdapter);
    }
}