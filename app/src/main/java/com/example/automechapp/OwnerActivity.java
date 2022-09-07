package com.example.automechapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class OwnerActivity extends AppCompatActivity {
    final Calendar calendar = Calendar.getInstance();
    Button saveButton;

    EditText birthdate;
    EditText name;
    EditText surname;
    EditText patronymic;
    EditText region;
    EditText driver_license;
    EditText issuing_region;
    EditText categories;
    EditText passport_series;
    EditText passport_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner);

        // тулбар, без него никуда
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Новый автовладелец");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        saveButton = findViewById(R.id.save_owner);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });

        DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                updateLabel();
            }
        };

        birthdate = findViewById(R.id.date_of_birth);
        birthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog date = new DatePickerDialog(OwnerActivity.this,
                        dateListener,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                Calendar c = Calendar.getInstance();
                c.set(c.get(Calendar.YEAR) - 18, c.get(Calendar.MONTH), c.get(Calendar.DATE));
                date.getDatePicker().setMaxDate(c.getTimeInMillis());
                date.show();
            }
        });

        name = findViewById(R.id.username);
        surname = findViewById(R.id.surname);
        patronymic = findViewById(R.id.patronymic);
        region = findViewById(R.id.region);
        driver_license = findViewById(R.id.driver_license);
        issuing_region = findViewById(R.id.issung_region);
        categories = findViewById(R.id.categories);
        passport_series = findViewById(R.id.passport_series);
        passport_number = findViewById(R.id.passport_number);
    }

    private void updateLabel(){
        String myFormat="dd.MM.yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        birthdate.setText(dateFormat.format(calendar.getTime()));
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences settings = getSharedPreferences(MainActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        editor.putInt(
                MainActivity.APP_PREFERENCES_CODE,
                R.id.nav_owners
        );
        editor.apply();
    }

    private ContentValues getValues() {
        ContentValues contentValues = new ContentValues();

        contentValues.put(DatabaseInfo.USERNAME, name.getText().toString());
        contentValues.put(DatabaseInfo.SURNAME, surname.getText().toString());
        contentValues.put(DatabaseInfo.PATRONYMIC, patronymic.getText().toString());
        contentValues.put(DatabaseInfo.REGION, region.getText().toString());
        contentValues.put(DatabaseInfo.DATE_OF_BIRTH, birthdate.getText().toString());
        contentValues.put(DatabaseInfo.ISSUING_REGION, issuing_region.getText().toString());
        contentValues.put(DatabaseInfo.CATEGORIES, categories.getText().toString());
        try {
            contentValues.put(DatabaseInfo.DRIVER_LICENSE, Long.parseLong(driver_license.getText().toString()));
            contentValues.put(DatabaseInfo.PASSPORT_SERIES, Integer.parseInt(passport_series.getText().toString()));
            contentValues.put(DatabaseInfo.PASSPORT_NUMBER, Integer.parseInt(passport_number.getText().toString()));
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

        return contentValues;
    }

    private void disableText() {
        saveButton.setEnabled(false);
        name.setEnabled(false);
        patronymic.setEnabled(false);
        surname.setEnabled(false);
        birthdate.setEnabled(false);
        region.setEnabled(false);
        driver_license.setEnabled(false);
        issuing_region.setEnabled(false);
        categories.setEnabled(false);
        passport_series.setEnabled(false);
        passport_number.setEnabled(false);
    }

    private void saveData() {
        ContentValues contentValues = getValues();
        if (contentValues == null) {
            Toast.makeText(this, "Введите данные", Toast.LENGTH_SHORT).show();
            return;
        }
        DatabaseInterface databaseInterface = new DatabaseInterface(this);
        databaseInterface.addData(
                contentValues,
                DatabaseInfo.OWNERS_TABLE
        );
        disableText();
    }
}