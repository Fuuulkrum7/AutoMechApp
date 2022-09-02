package com.example.automechapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import id.zelory.compressor.Compressor;

public class CarActivity extends AppCompatActivity {
    private static final int ICON_CODE = 0;
    private static final int PHOTO_CODE = 1;
    private int id = -1;

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

    int user_id = 0;

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    ViewPager2 imageSwitcher;
    int length_of_year = 0;

    int car_year = 0;
    ViewPagerAdapter viewPagerAdapter;
    ArrayList<Bitmap> bitmaps = new ArrayList<>();
    String currentPhotoPath;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car);

        Toolbar toolbar = findViewById(R.id.car_toolbar);
        toolbar.setTitle("Автомобиль");
        setSupportActionBar(toolbar);

        context = this;

        saveData = (Button) findViewById(R.id.save_button);
        saveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDataSave();
            }
        });

        name = (EditText) findViewById(R.id.name);
        manufacture = (EditText) findViewById(R.id.manufacture);
        model = (EditText) findViewById(R.id.model);
        price =(EditText) findViewById(R.id.price);
        color = (EditText) findViewById(R.id.color);
        vin = (EditText) findViewById(R.id.vin);
        engine_volume = (EditText) findViewById(R.id.engine_volume);
        engine_model = (EditText) findViewById(R.id.engine_model);
        engine_number = (EditText) findViewById(R.id.engine_number);
        car_state_number = (EditText) findViewById(R.id.car_state_number);
        tax = (EditText) findViewById(R.id.tax);
        horsepower = (EditText) findViewById(R.id.horsepower);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        year = findViewById(R.id.year);
        year.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    length_of_year = year.length();
                    car_year = Integer.parseInt(year.getText().toString());
                }
                catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                if (length_of_year == 4 && car_year < 1886) {
                    Toast.makeText(CarActivity.this, "Некорректный год", Toast.LENGTH_SHORT).show();
                }
            }
        });

        icon = findViewById(R.id.icon);
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (id < 0)
                    startCamera(ICON_CODE);
            }
        });

        getTextData = findViewById(R.id.count_tax);
        getTextData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO изменить на ручной рассчет
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.nalog.gov.ru/rn74/service/calc_transport/"));
                startActivity(browserIntent);
            }
        });

        imageSwitcher = findViewById(R.id.image_switcher);
        viewPagerAdapter = new ViewPagerAdapter(CarActivity.this, bitmaps);
        imageSwitcher.setAdapter(viewPagerAdapter);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            id = bundle.getInt("id");
            user_id = bundle.getInt("user_id");

            saveData.setVisibility(View.INVISIBLE);
            GetCurrentCar getCurrentCar = new GetCurrentCar(this, this, id);
            getCurrentCar.start();
            disableText();
        }
    }

    private void startDataSave() {
        SaveData save = new SaveData(this, getContext());
        save.start();
        saveData.setVisibility(View.INVISIBLE);
        disableText();
    }

    private void disableText() {
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

    @SuppressLint("QueryPermissionsNeeded")
    private void startCamera(int code) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create the File where the photo should go
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error hapened", Toast.LENGTH_SHORT).show();
        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(this,
                    "com.example.android.fileprovider",
                    photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, code);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.car_activity_menu, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                galleryAddPic(requestCode);
            }
            catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Произошла ошибка", Toast.LENGTH_SHORT).show();
            }
        }
    }

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

        bitmaps = car.getCar_photos();
        viewPagerAdapter = new ViewPagerAdapter(CarActivity.this, bitmaps);
        imageSwitcher.setAdapter(viewPagerAdapter);
    }

    public ContentValues getValues() {
        ContentValues contentValues = new ContentValues();

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

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        String date = sdf.format(c.getTime());

        contentValues.put(DatabaseInfo.STANDARD_DATE, date);

        return contentValues;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_photo:
                if (id < 0)
                    startCamera(PHOTO_CODE);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    static Context getContext() {
        return context;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void galleryAddPic(int code) throws IOException {
        Bitmap bitmap = null;
        try {
            File f = new File(currentPhotoPath);
            f = new Compressor(this)
                    .setMaxWidth(code == ICON_CODE ? 128 : 640)
                    .setMaxHeight(code == ICON_CODE ? 128 : 480)
                    .setQuality(50)
                    .compressToFile(f);
            Uri contentUri = Uri.fromFile(f);
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentUri);
            if (code == ICON_CODE) {
                bitmap = ImageUtil.getScaledBitmap(
                        ImageUtil.getSquaredBitmap(bitmap),
                        128,
                        128
                );

                icon.setImageBitmap(bitmap);
            }
            else if (code == PHOTO_CODE) {
                // TODO убрать костыль
                bitmaps.add(bitmap);
                viewPagerAdapter = new ViewPagerAdapter(CarActivity.this, bitmaps);
                imageSwitcher.setAdapter(viewPagerAdapter);
            }
            f.delete();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
        }
    }
}