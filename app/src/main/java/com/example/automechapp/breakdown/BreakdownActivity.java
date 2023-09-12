package com.example.automechapp.breakdown;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.automechapp.R;
import com.example.automechapp.ViewPagerAdapter;
import com.example.automechapp.camera_utils.ImageUtil;
import com.example.automechapp.camera_utils.PhotoWorker;
import com.example.automechapp.car.Car;
import com.example.automechapp.car.CarSpinnerAdapter;
import com.example.automechapp.database.DatabaseInfo;
import com.example.automechapp.database.GetBreakdowns;
import com.example.automechapp.database.GetCars;
import com.example.automechapp.database.GetOwners;
import com.example.automechapp.database.SaveBreakdown;
import com.example.automechapp.database.SaveCar;
import com.example.automechapp.detail.Detail;
import com.example.automechapp.owner.OwnerSpinnerAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class BreakdownActivity extends PhotoWorker {
    final Calendar calendar = Calendar.getInstance();

    // А это тоже id. Но пользователя, получаем его при добавлении, так тупо легче это все оформить.
    int car_id = 0;

    EditText breakdown_date;
    EditText work_price;
    EditText details_price;
    EditText breakdowns_price;
    EditText breakdown_name;
    EditText comment;
    EditText description;

    Spinner breakdown_state, breakdown_type, carsList;
    Button save_button, add_details_button;

    float det_price = 0;
    float sum = 0;

    ArrayList<Detail> details = new ArrayList<>();
    ArrayList<Car> cars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breakdown);

        // сохраняем на всякий контекст
        context = this;

        // тулбар, без него никуда
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Поломка");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        DatePickerDialog.OnDateSetListener dateListener = (view, year, month, day) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            updateDate();
        };

        // TODO uncomment
        // checkDetails();

        breakdown_date = findViewById(R.id.breakdown_date);
        breakdown_date.setOnClickListener(view -> {
            DatePickerDialog date = new DatePickerDialog(BreakdownActivity.this,
                    dateListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));

            date.getDatePicker().setMaxDate(new Date().getTime());
            date.show();
        });

        breakdowns_price = findViewById(R.id.breakdown_price);
        work_price = findViewById(R.id.work_price);
        details_price = findViewById(R.id.details_price);

        breakdown_name = findViewById(R.id.breakdown_name);
        comment = findViewById(R.id.comment);
        description = findViewById(R.id.description);

        breakdown_state = findViewById(R.id.breakdown_state);
        breakdown_type = findViewById(R.id.breakdown_type);
        carsList = findViewById(R.id.cars_list);

        add_details_button = findViewById(R.id.add_details);
        save_button = findViewById(R.id.save_breakdown);


        save_button.setOnClickListener(view -> startDataSave());

        work_price.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    sum = Float.parseFloat(s.toString()) + det_price;
                }
                else {
                    sum = det_price;
                }
                
                breakdowns_price.setText(Float.toString(sum));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        icon = findViewById(R.id.breakdown_icon);
        icon.setOnClickListener(view -> {
            // id меньше 0 тогда, когда авто только создается, т.е. фото делать надо
            if (id < 0)
                getUserImage(ICON_CODE);
        });

        // viewpager2 для перелистывания картинок в шапке
        imageSwitcher = findViewById(R.id.image_switcher);
        // Ставим адаптер
        viewPagerAdapter = new ViewPagerAdapter(context, bitmaps);
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
            car_id = bundle.getInt("car_id");

            // TODO add data load
            save_button.setVisibility(View.INVISIBLE);
            Thread thread = new Thread(() -> {
                GetBreakdowns getBreakdowns = new GetBreakdowns(
                        context,
                        DatabaseInfo.BREAKDOWN_ID + " = '" + id + "'",
                        null,
                        id
                );
                getBreakdowns.start();

                try {
                    getBreakdowns.join();
                    if (getBreakdowns.getData() != null) {
                        Breakdown breakdown = getBreakdowns.getData().get(0);
                        new Handler(Looper.getMainLooper()).post(() -> setData(breakdown));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                }
            );

            thread.start();
            disableText();
        }
        else {
            getCarsList();
        }
    }

    private void updateDate(){
        String myFormat="dd.MM.yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        breakdown_date.setText(dateFormat.format(calendar.getTime()));
    }

    public void getCarsList() {
        Thread thread = new Thread(() -> {
            GetCars getCars = new GetCars(getContext(), "", "");
            getCars.start();

            try {
                getCars.join();
                cars = getCars.getData();

                new Handler(Looper.getMainLooper()).post(() -> carsList.setAdapter(
                        new CarSpinnerAdapter(getContext(),
                                R.layout.spinner_dropdown_item, cars
                        )
                ));

                car_id = cars.get(0).getId();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });

        thread.start();
    }

    private void checkDetails() {
        Thread thread = new Thread(() -> {
            // TODO изменить на детали
            GetCars getCars = new GetCars(
                    getApplicationContext(),
                    "",
                    DatabaseInfo.STANDARD_DATE + " DESC"
            );
            // запуск потока и присоединение к нему
            getCars.start();
            try {
                getCars.join();
                for (Car car: getCars.getData()) {
                    det_price += car.getCar_price();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show());
                e.printStackTrace();
            }
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @SuppressLint("SetTextI18n")
                @Override
                public void run() {
                    details_price.setText(Float.toString(det_price));
                    sum += det_price;
                    breakdowns_price.setText(Float.toString(sum));
                }
            });
        });

        thread.start();
    }

    private void disableText() {
        Objects.requireNonNull(getSupportActionBar()).setTitle(breakdown_name.getText().toString());

        breakdown_name.setEnabled(false);
        breakdown_type.setEnabled(false);
        breakdown_state.setEnabled(false);
        breakdown_date.setEnabled(false);
        breakdowns_price.setEnabled(false);
        work_price.setEnabled(false);
        details_price.setEnabled(false);
        add_details_button.setEnabled(false);
        comment.setEnabled(false);
        description.setEnabled(false);
        carsList.setEnabled(false);
    }

    // Запуск сохранени данных
    private void startDataSave() {
        ContentValues contentValues = getValues();
        if (contentValues == null) {
            Toast.makeText(this, "Введите данные", Toast.LENGTH_SHORT).show();
            return;
        }
        // Создаем и запускаем поток для сохранения данных
        SaveBreakdown save = new SaveBreakdown(this, getContext(), contentValues);
        save.start();
        // Прячем кнопку
        save_button.setVisibility(View.INVISIBLE);
        // Убираем возможность для пользователя редактировать текст
        disableText();
    }

    // Парсим данные из текстовых перменных
    public ContentValues getValues() {
        ContentValues contentValues = new ContentValues();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        String date = sdf.format(c.getTime());

        try {
            contentValues.put(DatabaseInfo.BREAKDOWN_NAME, breakdown_name.getText().toString());
            contentValues.put(DatabaseInfo.BREAKDOWN_TYPE, breakdown_type.getSelectedItemPosition());
            contentValues.put(DatabaseInfo.BREAKDOWN_STATE, breakdown_state.getSelectedItemPosition());
            contentValues.put(DatabaseInfo.WORK_PRICE, Float.parseFloat(work_price.getText().toString()));
            contentValues.put(DatabaseInfo.STANDARD_COMMENT, comment.getText().toString());
            contentValues.put(DatabaseInfo.STANDARD_DESCRIPTION, description.getText().toString());
            contentValues.put(DatabaseInfo.EDIT_TIME, date);
            contentValues.put(DatabaseInfo.BREAKDOWN_PHOTO, ImageUtil.getBitmapAsByteArray((((BitmapDrawable) icon.getDrawable()).getBitmap())));
            contentValues.put(DatabaseInfo.CAR_ID, car_id);
            contentValues.put(DatabaseInfo.STANDARD_DATE, date);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }

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

    // Парсим данные из экземпляра машины
    @SuppressLint("SetTextI18n")
    public void setData(Breakdown breakdown) {
        Objects.requireNonNull(getSupportActionBar()).setTitle(breakdown.getBreakdown_name());

        for (Detail detail : details)
            sum += detail.getPrice();

        details_price.setText(Float.toString(sum));

        breakdown_name.setText(breakdown.getBreakdown_name());
        breakdown_date.setText(breakdown.getDate());
        // В этот момент мы сразу добавляем к итоговой сумме стоимость работы
        work_price.setText(Float.toString(breakdown.getWork_price()));
        breakdown_state.setSelection(breakdown.getBreakdown_state().ordinal());
        breakdown_type.setSelection(breakdown.getBreakdown_state().ordinal());
        description.setText(breakdown.getDescription());
        comment.setText(breakdown.getComment());
        icon.setImageBitmap(breakdown.getIcon());

        car_id = breakdown.getCar_id();


        bitmaps = breakdown.getPhotos();
        setImages();

        Thread thread = new Thread(() -> {
            GetCars getCars = new GetCars(getContext(), DatabaseInfo.CAR_ID + " = " + car_id, "");
            getCars.start();

            try {
                getCars.join();
                cars = getCars.getData();

                new Handler(Looper.getMainLooper()).post(() ->
                        carsList.setAdapter(
                                new CarSpinnerAdapter(
                                        getContext(),
                                        R.layout.spinner_dropdown_item, cars
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
}