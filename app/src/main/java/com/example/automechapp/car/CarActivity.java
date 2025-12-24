package com.example.automechapp.car;

import static com.example.automechapp.database.DatabaseInfo.CARS_TABLE;
import static com.example.automechapp.database.DatabaseInfo.CAR_ID;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.automechapp.MainActivity;
import com.example.automechapp.R;
import com.example.automechapp.ViewPagerAdapter;
import com.example.automechapp.camera_utils.ImageUtil;
import com.example.automechapp.camera_utils.PhotoWorker;
import com.example.automechapp.database.DatabaseInfo;
import com.example.automechapp.database.DatabaseInterface;
import com.example.automechapp.database.DeleteCars;
import com.example.automechapp.database.GetCurrentCar;
import com.example.automechapp.database.GetOwners;
import com.example.automechapp.database.SaveCar;
import com.example.automechapp.owner.Owner;
import com.example.automechapp.owner.OwnerSpinnerAdapter;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;


public class CarActivity extends PhotoWorker {

    // Тут просто один коммент, это перменные для хранения всех полей текстовых
    EditText year;
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
    boolean normal_volume = false;

    // А это тоже id. Но пользователя, получаем его при добавлении, так тупо легче это все оформить.
    int user_id = 0;

    int length_of_year = 0;

    // Сам год числом
    int car_year = 0;

    ArrayList<Owner> owners;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car);

        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_layout);
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_NO:
            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                collapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);
                break;
        }

        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(listener);

        // тулбар, без него никуда
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Автомобиль");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.arrow_back);

        // сохраняем на всякий контекст
        context = this;

        // Кнопка сохранения данных
        saveData = findViewById(R.id.save_button);
        saveData.setOnClickListener(view -> startDataSave());

        // Все поля ввода данных
        name = findViewById(R.id.name);
        manufacture = findViewById(R.id.manufacture);
        model = findViewById(R.id.model);
        price = findViewById(R.id.price);
        color = findViewById(R.id.color);
        vin = findViewById(R.id.vin);
        engine_volume = findViewById(R.id.engine_volume);
        engine_model = findViewById(R.id.engine_model);
        engine_number = findViewById(R.id.engine_number);
        car_state_number = findViewById(R.id.car_state_number);
        tax = findViewById(R.id.tax);
        horsepower = findViewById(R.id.horsepower);
        ownersSpinner = findViewById(R.id.spinner);

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
                if (length_of_year == 4 && (car_year < 1886 || car_year > Calendar.getInstance().get(Calendar.YEAR))) {
                    Toast.makeText(CarActivity.this, "Некорректный год", Toast.LENGTH_SHORT).show();
                }


                allowed = length_of_year == 4 && car_year >= 1886 && car_year <= Calendar.getInstance().get(Calendar.YEAR);
            }
        });

        engine_volume.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    float a = Float.parseFloat(s.toString());

                    normal_volume = a > 0 && a <= 16.1;
                    if (!normal_volume)
                        Toast.makeText(CarActivity.this, "Недопустимый объем двигателя", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Иконка
        icon = findViewById(R.id.car_icon);
        icon.setOnClickListener(view -> {
            // id меньше 0 тогда, когда авто только создается, т.е. фото делать надо
            if (id < 0)
                getUserImage(ICON_CODE);
        });

        // Переход на сайт для рассчета налога
        getTextData = findViewById(R.id.count_tax);
        getTextData.setOnClickListener(view -> {
            // TODO изменить на ручной рассчет
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.nalog.gov.ru/rn74/service/calc_transport/"));
            startActivity(browserIntent);
        });

        // viewpager2 для перелистывания картинок в шапке
        imageSwitcher = findViewById(R.id.image_switcher);
        // Ставим адаптер
        viewPagerAdapter = new ViewPagerAdapter(CarActivity.this, bitmaps);
        imageSwitcher.setAdapter(viewPagerAdapter);

        // И прослушку
        imageSwitcher.setOnClickListener(view -> {

        });

        Bundle bundle = getIntent().getExtras();

        // получаеа данные, если есть id, то загружаем по нему авто
        if (bundle != null) {
            id = bundle.getInt("id");
            user_id = bundle.getInt("user_id");
            edit = bundle.getBoolean("edit", false);

            saveData.setVisibility(View.INVISIBLE);
            GetCurrentCar getCurrentCar = new GetCurrentCar(this, id);
            getCurrentCar.start();

            changeState(edit);
        }
        else
            getOwnersList();

        ownersSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                user_id = owners.get(i).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
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
        Thread thread = new Thread(() -> {
            GetOwners getOwners = new GetOwners(getContext(), "");
            getOwners.start();

            try {
                getOwners.join();
                owners = getOwners.getData();

                new Handler(Looper.getMainLooper()).post(() -> ownersSpinner.setAdapter(
                        new OwnerSpinnerAdapter(getContext(),
                                R.layout.spinner_dropdown_item, owners
                        )
                ));

                user_id = owners.get(0).getId();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });

        thread.start();
    }

    private void startUpdate() {
        if (id <= 0) {
            Toast.makeText(this, "В текущий момент id машины для обновления не получен", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = getValues();

        DatabaseInterface database = new DatabaseInterface(this);
        database.UpdateData(CARS_TABLE, values, null, CAR_ID + " = " + id);
    }

    // Запуск сохранени данных
    private void startDataSave() {
        ContentValues contentValues = getValues();
        if (contentValues == null) {
            return;
        }

        if (edit) {
            startUpdate();
            edit = false;
        }
        else {
            // Создаем и запускаем поток для сохранения данных
            SaveCar save = new SaveCar(this, getContext(), contentValues);
            save.start();
        }
        // Убираем возможность для пользователя редактировать текст
        changeState(false);
    }

    protected void changeState(boolean state) {
        super.changeState(state);

        // Ставим имя как заголовок
        Objects.requireNonNull(getSupportActionBar()).setTitle(name.getText().toString());

        // Вырубаем текст
        manufacture.setEnabled(state);
        model.setEnabled(state);
        year.setEnabled(state);
        car_state_number.setEnabled(state);
        name.setEnabled(state);
        vin.setEnabled(state);
        engine_number.setEnabled(state);
        engine_model.setEnabled(state);
        engine_volume.setEnabled(state);
        tax.setEnabled(state);
        price.setEnabled(state);
        color.setEnabled(state);
        horsepower.setEnabled(state);
        ownersSpinner.setEnabled(state);

        // Прячем кнопку
        if (state)
            saveData.setVisibility(View.VISIBLE);
        else
            saveData.setVisibility(View.INVISIBLE);
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

        // Удаляем фильтр
        car_state_number.setFilters(new InputFilter[] {});
        car_state_number.setText(car.getCar_state_number());

        tax.setText(Integer.toString(car.getTax()));
        horsepower.setText(Integer.toString(car.getHorsepower()));
        icon.setImageBitmap(car.getIcon());

        user_id = car.getUser_id();

        getSupportActionBar().setTitle(car.getCarName());

        bitmaps = car.getCar_photos();
        setImages();

        Thread thread = new Thread(() -> {
            GetOwners getOwners = new GetOwners(getContext(), DatabaseInfo.OWNER_ID + " = " + user_id);
            getOwners.start();

            try {
                getOwners.join();
                owners = getOwners.getData();

                new Handler(Looper.getMainLooper()).post(() ->
                        ownersSpinner.setAdapter(
                                new OwnerSpinnerAdapter(
                                        getContext(),
                                        R.layout.spinner_dropdown_item, owners
                                )
                        )
                );
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });

        thread.start();
    }

    @Override
    protected void deleteData() {
        ArrayList<Integer> arr = new ArrayList<>();
        arr.add(id);

        DeleteCars deleteCars = new DeleteCars(MainActivity.getContext(), arr);
        deleteCars.start();
        super.finish();
    }

    // Парсим данные из текстовых перменных
    public ContentValues getValues() {
        if (!normal_volume) {
            Toast.makeText(this, "Некорректный объем двигателя", Toast.LENGTH_SHORT).show();
            return null;
        }
        if (!allowed) {
            Toast.makeText(this, "Некорректный год", Toast.LENGTH_SHORT).show();
            return null;
        }

        ContentValues contentValues = new ContentValues();
        int idx = 0;
        try {
            contentValues.put(DatabaseInfo.CAR_NAME, name.getText().toString());
            if (name.getText().toString().length() < 4)
                throw new Exception("Incorrect car name");
            ++idx;
            contentValues.put(DatabaseInfo.CAR_MANUFACTURE, manufacture.getText().toString());
            if (manufacture.getText().toString().length() < 3)
                throw new Exception("Incorrect car manufacture");
            ++idx;
            contentValues.put(DatabaseInfo.CAR_MODEL, model.getText().toString());
            if (model.getText().toString().length() < 3)
                throw new Exception("Incorrect car model");
            ++idx;
            contentValues.put(DatabaseInfo.CAR_YEAR, Integer.parseInt(year.getText().toString()));
            ++idx;
            contentValues.put(DatabaseInfo.CAR_PRICE, Long.parseLong(price.getText().toString()));
            ++idx;
            contentValues.put(DatabaseInfo.COLOR, color.getText().toString());
            if (color.getText().toString().length() > 0)
                contentValues.put(DatabaseInfo.COLOR, color.getText().toString());
            else
                contentValues.put(DatabaseInfo.COLOR, "-");
            ++idx;
            contentValues.put(DatabaseInfo.VIN, vin.getText().toString());
            if (vin.getText().toString().length() > 0)
                contentValues.put(DatabaseInfo.VIN, vin.getText().toString());
            else
                contentValues.put(DatabaseInfo.VIN, "-");
            ++idx;
            contentValues.put(DatabaseInfo.ENGINE_VOLUME, Float.parseFloat(engine_volume.getText().toString()));
            ++idx;
            contentValues.put(DatabaseInfo.ENGINE_MODEL, engine_model.getText().toString());
            if (engine_model.getText().toString().length() > 0)
                contentValues.put(DatabaseInfo.ENGINE_MODEL, vin.getText().toString());
            else
                contentValues.put(DatabaseInfo.ENGINE_MODEL, "-");
            ++idx;
            contentValues.put(DatabaseInfo.ENGINE_NUMBER, engine_number.getText().toString());
            if (vin.getText().toString().length() > 0)
                contentValues.put(DatabaseInfo.ENGINE_NUMBER, engine_number.getText().toString());
            else
                contentValues.put(DatabaseInfo.ENGINE_NUMBER, "-");
            ++idx;
            contentValues.put(DatabaseInfo.STATE_CAR_NUMBER, car_state_number.getText().toString());
            if (car_state_number.getText().toString().length() < 8)
                throw new Exception("Incorrect car number");
            ++idx;
            contentValues.put(DatabaseInfo.TAX, Integer.parseInt(tax.getText().toString()));
            ++idx;
            contentValues.put(DatabaseInfo.CAR_PHOTO, ImageUtil.getBitmapAsByteArray((((BitmapDrawable) icon.getDrawable()).getBitmap())));
            ++idx;
            contentValues.put(DatabaseInfo.HORSEPOWER, Integer.parseInt(horsepower.getText().toString()));
            ++idx;
            contentValues.put(DatabaseInfo.OWNER_ID, user_id);
        }
        catch (Exception e) {
            e.printStackTrace();
            switch (idx) {
                case 0:
                    Toast.makeText(this, "Некорректное прозвище для машины (длина менее 4 символов)", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Toast.makeText(this, "Некорректная марка авто", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(this, "Некорректное название модели", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(this, "Некорректный год", Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    Toast.makeText(this, "Некорректная стоимость", Toast.LENGTH_SHORT).show();
                    break;
                case 5:
                    Toast.makeText(this, "Некорректный цвет", Toast.LENGTH_SHORT).show();
                    break;
                case 6:
                    Toast.makeText(this, "Некорректный VIN", Toast.LENGTH_SHORT).show();
                    break;
                case 7:
                    Toast.makeText(this, "Некорректный объем двигателя", Toast.LENGTH_SHORT).show();
                    break;
                case 8:
                    Toast.makeText(this, "Некорректная модель двигателя", Toast.LENGTH_SHORT).show();
                    break;
                case 9:
                    Toast.makeText(this, "Некорректный номер двигателя", Toast.LENGTH_SHORT).show();
                    break;
                case 10:
                    Toast.makeText(this, "Некорректный номер авто", Toast.LENGTH_SHORT).show();
                    break;
                case 11:
                    Toast.makeText(this, "Некорректный налог", Toast.LENGTH_SHORT).show();
                    break;
                case 12:
                    Toast.makeText(this, "Нет иконки авто", Toast.LENGTH_SHORT).show();
                    break;
                case 13:
                    Toast.makeText(this, "Некорректная мощность", Toast.LENGTH_SHORT).show();
                    break;
            }
            return null;
        }

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        String date = sdf.format(c.getTime());

        contentValues.put(DatabaseInfo.STANDARD_DATE, date);

        for (String s: contentValues.keySet()) {
            if (contentValues.get(s) == null) {
                Toast.makeText(this, "Некорректные данные", Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        if (bitmaps.size() == 0) {
            Toast.makeText(this, "Нет фото авто", Toast.LENGTH_SHORT).show();
            return null;
        }

        return contentValues;
    }
}