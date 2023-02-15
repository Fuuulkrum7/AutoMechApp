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

import com.example.automechapp.database.DatabaseInfo;
import com.example.automechapp.database.GetCars;
import com.example.automechapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class BreakdownsFragment extends Fragment {
    // Список из поломок, кнопка для добавления новой поломки, то, где будут все поломки
    private final ArrayList<Breakdown> breakdowns = new ArrayList<Breakdown>();
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
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count > 0) {
                    Intent intent = new Intent(getContext(), BreakdownActivity.class);
                    getContext().startActivity(intent);
                }
                else {
                    Toast.makeText(v.getContext(), "Сначала добавьте автомобиль", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    // Задаем стартовые данные
    private void setInitialData() {
        breakdowns.add(new Breakdown("колесо пробил", "Toyota", "Supra", null, "12.12.12", "00:00", 0, 0));
        breakdowns.add(new Breakdown("колесо пробил", "Toyota", "Supra", null, "12.12.12", "00:00", 0, 0));
        breakdowns.add(new Breakdown("колесо пробил", "Toyota", "Supra", null, "12.12.12", "00:00", 0, 0));
        breakdowns.add(new Breakdown("колесо пробил", "Toyota", "Supra", null, "12.12.12", "00:00", 0, 0));
        breakdowns.add(new Breakdown("колесо пробил", "Toyota", "Supra", null, "12.12.12", "00:00", 0, 0));
        breakdowns.add(new Breakdown("колесо пробил", "Toyota", "Supra", null, "12.12.12", "00:00", 0, 0));
        breakdowns.add(new Breakdown("колесо пробил", "Toyota", "Supra", null, "12.12.12", "00:00", 0, 0));
        breakdowns.add(new Breakdown("колесо пробил", "Toyota", "Supra", null, "12.12.12", "00:00", 0, 0));
    }

    private void checkCars() {
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
                    count = getCars.getData().size();
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
            }
        };

        thread.start();
    }
}
