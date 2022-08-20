package com.example.automechapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

public class CarActivity extends AppCompatActivity {
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Автомобиль");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            id = bundle.getInt("id");
        }
    }
}