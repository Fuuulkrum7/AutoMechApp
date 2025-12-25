package com.example.automechapp.database;

import static com.example.automechapp.database.DatabaseInfo.*;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.automechapp.MainActivity;
import com.example.automechapp.car.CarActivity;

import java.util.Arrays;

public class DatabaseInterface extends SQLiteOpenHelper {
    // Данные по бд
    public static final int DATABASE_VERSION = 8;

    // Инициализация, ничего интересного
    public DatabaseInterface(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase db = getWritableDatabase();
    }

    // Инициализируем бд
    @Override
    public void onCreate(SQLiteDatabase db) {
        for (String s : Arrays.asList(
                CREATE_OWNERS_TABLE,
                CREATE_CARS_TABLE,
                CREATE_BREAKDOWNS_TABLE,
                CREATE_DETAILS_TABLE,
                CREATE_CAR_PHOTOS_TABLE,
                CREATE_BREAKDOWNS_PHOTOS_TABLE,
                CREATE_DETAILS_PHOTOS_TABLE,
                CREATE_DOCUMENTS_TABLE)) {
            db.execSQL(s);
        }
    }

    // Обновление бд
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        clearAllTables(db);
        onCreate(db);
    }

    // Удвление таблиц
    private void clearAllTables(SQLiteDatabase db) {
        db.execSQL(DELETE_OWNERS_TABLE);
        db.execSQL(DELETE_CARS_TABLE);
        db.execSQL(DELETE_BREAKDOWNS_TABLE);
        db.execSQL(DELETE_DETAILS_TABLE);
        db.execSQL(DELETE_CAR_PHOTOS_TABLE);
        db.execSQL(DELETE_BREAKDOWNS_PHOTOS_TABLE);
        db.execSQL(DELETE_DETAILS_PHOTOS_TABLE);
        db.execSQL(DELETE_DOCUMENTS_TABLE);
    }

    // Метод добавления данных в бд
    public long addData(ContentValues values, String table){
        SQLiteDatabase db = getWritableDatabase();
        // Добавляем в бд
        try {
            return db.insert(table, null, values);
        }
        // Если что-то пошло не так, то вот
        catch (Exception e) {
            Log.d("TEST", e.toString());
            Toast toast = Toast.makeText(MainActivity.getContext(),
                    "Не удалось добавить данные", Toast.LENGTH_SHORT);

            toast.show();
        }
        return 0;
    }

    // Получение данных из бд
    public void getData(GetData<?> conn) {
        conn.moveDb(getReadableDatabase());
    }

    public void deleteData(String table, String[] projection, String selection) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            db.delete(
                    table,
                    selection,
                    projection
            );
        } catch (Exception e) {
            // Если что-то пошло не так
            Log.d(MainActivity.TAG, e.toString());

            new Handler(Looper.getMainLooper()).post(() -> {
                Context context = MainActivity.getContext();
                if (context == null) {
                    context = CarActivity.getContext();
                }
                Toast toast = Toast.makeText(context,
                        "Не удалось удалить данные", Toast.LENGTH_SHORT);
                toast.show();
            });
        }
    }

    public void UpdateData(String table, ContentValues values, String[] whereArgs, String whereClause) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            db.update(
                    table,
                    values,
                    whereClause,
                    whereArgs
            );
        } catch (Exception e) {
            // Если что-то пошло не так
            Log.d(MainActivity.TAG, e.toString());

            new Handler(Looper.getMainLooper()).post(() -> {
                Context context = MainActivity.getContext();
                if (context == null) {
                    context = CarActivity.getContext();
                }
                Toast toast = Toast.makeText(context,
                        "Не удалось обновить данные", Toast.LENGTH_SHORT);
                toast.show();
            });
        }
    }
}
