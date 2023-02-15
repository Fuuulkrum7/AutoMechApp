package com.example.automechapp.car;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.automechapp.database.DatabaseInfo;
import com.example.automechapp.database.GetCars;
import com.example.automechapp.database.GetOwners;
import com.example.automechapp.MainActivity;
import com.example.automechapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

// Фрагмент авто
public class CarsFragment extends Fragment {
    // Список авто и тд
    private ArrayList<Car> cars = new ArrayList<Car>();
    public FloatingActionButton addButton;
    public RecyclerView carsView;

    int count = 0;

    // Инициализация фрагмента
    @Nullable
    @Override
    public View onCreateView
            (@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler,
                container, false);

        // поучаем контейнер для хранения в нем авто и тп
        carsView = view.findViewById(R.id.main_recycler);

        // запуск потока для получения данных и добавления фрагментов авто
        Thread thread = new Thread() {
            @Override
            public void run() {
                // Создаем класс для получения авто из бд
                GetCars getCars = new GetCars(
                        getContext(),
                        new String[]{
                            DatabaseInfo.CAR_NAME,
                            DatabaseInfo.CAR_MANUFACTURE,
                            DatabaseInfo.CAR_MODEL,
                            DatabaseInfo.CAR_ID,
                            DatabaseInfo.CAR_PHOTO,
                            DatabaseInfo.OWNER_ID
                        },
                        null,
                        DatabaseInfo.STANDARD_DATE + " DESC"
                        );
                // запуск потока и присоединение к нему
                getCars.start();
                try {
                    getCars.join();
                    cars = getCars.getData();
                }
                catch (Exception e) {
                    e.printStackTrace();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();
                        }
                    });
                    e.printStackTrace();

                }
                // Создаем адаптер
                CarsAdapter adapter = new CarsAdapter(MainActivity.getContext(), cars);

                // и добавляем его
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        carsView.setAdapter(adapter);
                    }
                });
            }
        };

        thread.start();

        checkOwners();

        // Добавляем прослушку на кнопку
        addButton = getActivity().findViewById(R.id.add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count > 0) {
                    Intent intent = new Intent(getContext(), CarActivity.class);
                    getContext().startActivity(intent);
                }
                else {
                    Toast.makeText(getContext(), "Сначала добавьте хотя бы одного автовладельца", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void checkOwners() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                GetOwners getOwners = new GetOwners(getContext(), null);
                getOwners.start();

                try {
                    getOwners.join();
                    count = getOwners.getOwnersList().size();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();
    }
}
