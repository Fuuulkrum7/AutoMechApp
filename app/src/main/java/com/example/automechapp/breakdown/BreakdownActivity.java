package com.example.automechapp.breakdown;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.automechapp.car.Car;
import com.example.automechapp.database.DatabaseInfo;
import com.example.automechapp.database.GetCars;
import com.example.automechapp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class BreakdownActivity extends AppCompatActivity {
    final Calendar calendar = Calendar.getInstance();

    EditText breakdown_date;
    EditText work_price;
    EditText details_price;
    EditText breakdowns_price;

    int price = 0;
    int sum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breakdown);


        // тулбар, без него никуда
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Поломка");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                updateDate();
            }
        };

        checkDetails();

        breakdown_date = findViewById(R.id.breakdown_date);
        breakdown_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog date = new DatePickerDialog(BreakdownActivity.this,
                        dateListener,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));

                date.getDatePicker().setMaxDate(new Date().getTime());
                date.show();
            }
        });

        breakdowns_price = findViewById(R.id.breakdown_price);
        work_price = findViewById(R.id.work_price);
        details_price = findViewById(R.id.details_price);
    }

    private void updateDate(){
        String myFormat="dd.MM.yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        breakdown_date.setText(dateFormat.format(calendar.getTime()));
    }

    private void checkDetails() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                // TODO изменить на детали
                GetCars getCars = new GetCars(
                        getApplicationContext(),
                        new String[]{
                                DatabaseInfo.CAR_ID,
                                DatabaseInfo.OWNER_ID,
                                DatabaseInfo.CAR_MODEL,
                                DatabaseInfo.CAR_MANUFACTURE,
                                DatabaseInfo.CAR_NAME,
                                DatabaseInfo.CAR_PHOTO,
                                DatabaseInfo.CAR_YEAR,
                                DatabaseInfo.CAR_PRICE,
                                DatabaseInfo.VIN,
                                DatabaseInfo.ENGINE_MODEL,
                                DatabaseInfo.ENGINE_NUMBER,
                                DatabaseInfo.ENGINE_VOLUME,
                                DatabaseInfo.CAR_YEAR,
                                DatabaseInfo.STATE_CAR_NUMBER,
                                DatabaseInfo.TAX,
                                DatabaseInfo.COLOR,
                                DatabaseInfo.HORSEPOWER
                        },
                        "",
                        DatabaseInfo.STANDARD_DATE + " DESC"
                );
                // запуск потока и присоединение к нему
                getCars.start();
                try {
                    getCars.join();
                    for (Car car: getCars.getData()) {
                        price += car.getCar_price();
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
                        }
                    });
                    e.printStackTrace();
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        details_price.setText(Integer.toString(price));
                        sum += price;
                        breakdowns_price.setText(Integer.toString(sum));
                    }
                });
            }
        };

        thread.start();
    }
}