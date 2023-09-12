package com.example.automechapp.breakdown;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.automechapp.MainActivity;
import com.example.automechapp.car.CarsAdapter;
import com.example.automechapp.database.DatabaseInfo;
import com.example.automechapp.database.GetBreakdowns;
import com.example.automechapp.database.GetCars;
import com.example.automechapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class BreakdownsFragment extends Fragment {
    // Список из поломок, кнопка для добавления новой поломки, то, где будут все поломки
    private ArrayList<Breakdown> breakdowns = new ArrayList<Breakdown>();
    public FloatingActionButton addButton;
    public RecyclerView breakdownsView;

    int count;

    // Инициализация фрагмента
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_recycler,
                container, false);

        // Задаем стартовые данные TODO сделать добавление даных из бд
        setInitialData();

        // Получаем контейнер для поломок
        breakdownsView = view.findViewById(R.id.main_recycler);

        // Создаем адаптер и ставим адаптер
        BreakdownsAdapter adapter = new BreakdownsAdapter(getContext(), breakdowns);
        breakdownsView.setAdapter(adapter);

        checkCars();

        // Ставим прослушку на кнопку
        addButton = getActivity().findViewById(R.id.add);
        addButton.setOnClickListener(v -> {
            if (count > 0) {
                Intent intent = new Intent(getContext(), BreakdownActivity.class);
                getContext().startActivity(intent);
            }
            else {
                Toast.makeText(v.getContext(), "Сначала добавьте автомобиль", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    // Задаем стартовые данные
    private void setInitialData() {
        // запуск потока для получения данных и добавления фрагментов авто
        Thread thread = new Thread() {
            @Override
            public void run() {
                // Создаем класс для получения авто из бд
                GetBreakdowns getBreakdowns = new GetBreakdowns(
                        getContext(),
                        null,
                        DatabaseInfo.STANDARD_DATE + " DESC"
                );
                // запуск потока и присоединение к нему
                getBreakdowns.start();
                try {
                    getBreakdowns.join();
                    breakdowns = getBreakdowns.getData();
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
                BreakdownsAdapter adapter = new BreakdownsAdapter(MainActivity.getContext(), breakdowns);

                // и добавляем его
                getActivity().runOnUiThread(() -> breakdownsView.setAdapter(adapter));
            }
        };

        thread.start();
    }

    private void checkCars() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                // Создаем класс для получения авто из бд
                GetCars getCars = new GetCars(
                        getContext(),
                        null,
                        DatabaseInfo.STANDARD_DATE + " DESC"
                );
                // запуск потока и присоединение к нему
                getCars.start();
                try {
                    getCars.join();
                    count = getCars.getData().size();
                }
                catch (Exception e) {
                    e.printStackTrace();
                    new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show());
                    e.printStackTrace();
                }
            }
        };

        thread.start();
    }
}
