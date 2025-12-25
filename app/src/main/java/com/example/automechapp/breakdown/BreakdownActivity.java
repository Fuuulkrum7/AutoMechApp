package com.example.automechapp.breakdown;

import static com.example.automechapp.database.DatabaseInfo.BREAKDOWNS_TABLE;
import static com.example.automechapp.database.DatabaseInfo.BREAKDOWN_ID;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.example.automechapp.car.Car;
import com.example.automechapp.car.CarSpinnerAdapter;
import com.example.automechapp.database.DatabaseInfo;
import com.example.automechapp.database.DatabaseInterface;
import com.example.automechapp.database.DeleteBreakdowns;
import com.example.automechapp.database.GetBreakdowns;
import com.example.automechapp.database.GetCars;
import com.example.automechapp.database.SaveBreakdown;
import com.example.automechapp.detail.Detail;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

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

        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_layout);
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_NO:
            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                collapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);
                break;
        }

        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(listener);

        // сохраняем на всякий контекст
        context = this;

        // тулбар, без него никуда
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Поломка");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.arrow_back);

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

        carsList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                car_id = cars.get(position).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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
        imageSwitcher.setOnClickListener(view -> {

        });

        Bundle bundle = getIntent().getExtras();

        // получаеа данные, если есть id, то загружаем по нему авто
        if (bundle != null) {
            id = bundle.getInt("id");
            car_id = bundle.getInt("car_id");
            edit = bundle.getBoolean("edit", false);

            // TODO add data load
            save_button.setVisibility(View.INVISIBLE);
            GetBreakdowns getBreakdowns = new GetBreakdowns(
                    context,
                    DatabaseInfo.BREAKDOWN_ID + " = '" + id + "'",
                    null,
                    id
            );
            getBreakdowns.setRunnable(() -> {
                if (getBreakdowns.getData() != null) {
                    Breakdown breakdown = getBreakdowns.getData().get(0);
                    setData(breakdown);
                }
            });
            getBreakdowns.start();
            changeState(edit);
        }
        else {
            getCarsList();
        }
    }

    @Override
    protected void deleteData() {
        ArrayList<Integer> arr = new ArrayList<>();
        arr.add(id);

        DeleteBreakdowns deleteBreakdowns = new DeleteBreakdowns(MainActivity.getContext(), arr);
        deleteBreakdowns.start();
        super.finish();
    }

    private void startUpdate() {
        if (id <= 0) {
            Toast.makeText(this, "В текущий момент id поломки для обновления не получен", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = getValues();

        DatabaseInterface database = new DatabaseInterface(this);
        database.UpdateData(BREAKDOWNS_TABLE, values, null, BREAKDOWN_ID + " = " + id);
    }

    private void updateDate(){
        String myFormat="dd.MM.yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        breakdown_date.setText(dateFormat.format(calendar.getTime()));
    }

    public void getCarsList() {
        GetCars getCars = new GetCars(getContext(), "", "");
        getCars.setRunnable(() -> {
            cars = getCars.getData();
            new CarSpinnerAdapter(getContext(),
                    R.layout.spinner_dropdown_item, cars
            );
            car_id = cars.get(0).getId();
        });
        getCars.start();
    }

    @SuppressLint("SetTextI18n")
    private void checkDetails() {
        // TODO изменить на детали
        GetCars getCars = new GetCars(
                getApplicationContext(),
                "",
                DatabaseInfo.STANDARD_DATE + " DESC"
        );
        getCars.setRunnable(() -> {
            for (Car car: getCars.getData()) {
                det_price += car.getCar_price();
            }
            details_price.setText(Float.toString(det_price));
            sum += det_price;
            breakdowns_price.setText(Float.toString(sum));
        });
        // запуск потока и присоединение к нему
        getCars.start();
    }

    protected void changeState(boolean state) {
        super.changeState(state);

        Objects.requireNonNull(getSupportActionBar()).setTitle(breakdown_name.getText().toString());

        breakdown_name.setEnabled(state);
        breakdown_type.setEnabled(state);
        breakdown_state.setEnabled(state);
        breakdown_date.setEnabled(state);
        breakdowns_price.setEnabled(state);
        work_price.setEnabled(state);
        details_price.setEnabled(state);
        add_details_button.setEnabled(state);
        comment.setEnabled(state);
        description.setEnabled(state);
        carsList.setEnabled(state);

        // Прячем кнопку
        if (state)
            save_button.setVisibility(View.VISIBLE);
        else
            save_button.setVisibility(View.INVISIBLE);
    }

    // Запуск сохранени данных
    private void startDataSave() {
        ContentValues contentValues = getValues();
        if (contentValues == null)
            return;

        if (edit) {
            startUpdate();
            edit = false;
        }
        else {
            // Создаем и запускаем поток для сохранения данных
            SaveBreakdown save = new SaveBreakdown(this, getContext(), contentValues);
            save.start();
        }

        // Убираем возможность для пользователя редактировать текст
        changeState(false);
    }

    // Парсим данные из текстовых перменных
    public ContentValues getValues() {
        ContentValues contentValues = new ContentValues();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        String date = sdf.format(c.getTime());

        boolean flag = true;
        try {
            contentValues.put(DatabaseInfo.BREAKDOWN_NAME, breakdown_name.getText().toString());
            if (breakdown_name.getText().toString().length() < 4) {
                Toast.makeText(this, "Введите название поломки (должно быть более 4 символов)", Toast.LENGTH_SHORT).show();
                return null;
            }

            if (work_price.getText().toString().isEmpty())
                contentValues.put(DatabaseInfo.WORK_PRICE, 0);
            else
                contentValues.put(DatabaseInfo.WORK_PRICE, Float.parseFloat(work_price.getText().toString()));
            contentValues.put(DatabaseInfo.STANDARD_COMMENT, comment.getText().toString());
            contentValues.put(DatabaseInfo.STANDARD_DESCRIPTION, description.getText().toString());
            contentValues.put(DatabaseInfo.EDIT_TIME, date);
            contentValues.put(DatabaseInfo.BREAKDOWN_PHOTO, ImageUtil.getBitmapAsByteArray((((BitmapDrawable) icon.getDrawable()).getBitmap())));
            flag = false;
            contentValues.put(DatabaseInfo.CAR_ID, car_id);
            contentValues.put(DatabaseInfo.STANDARD_DATE, breakdown_date.getText().toString());
            if (breakdown_date.getText().toString().isEmpty()) {
                Toast.makeText(this, "Введите дату поломки", Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        catch (Exception e) {
            e.printStackTrace();
            if (flag)
                Toast.makeText(this, "Добаьте иконку поломки", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "Введите данные", Toast.LENGTH_SHORT).show();
            return null;
        }

        for (String s: contentValues.keySet()) {
            if (contentValues.get(s) == null) {
                Toast.makeText(this, "Введите данные", Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        if (bitmaps.isEmpty()) {
            Toast.makeText(this, "Добавьте фото поломки", Toast.LENGTH_SHORT).show();
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

        GetCars getCars = new GetCars(getContext(), DatabaseInfo.CAR_ID + " = " + car_id, "");
        getCars.setRunnable(() -> {
            cars = getCars.getData();
            if (cars == null || cars.isEmpty()) {
                return;
            }
            carsList.setAdapter(
                    new CarSpinnerAdapter(
                            getContext(),
                            R.layout.spinner_dropdown_item, cars
                    )
            );
        });
        getCars.start();
    }
}