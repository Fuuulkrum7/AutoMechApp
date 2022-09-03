package com.example.automechapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class BreakdownsFragment extends Fragment {
    // Список из поломок, кнопка для добавления новой поломки, то, где будут все поломки
    private final ArrayList<Breakdown> breakdowns = new ArrayList<Breakdown>();
    public FloatingActionButton addButton;
    public RecyclerView breakdownsView;

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

        // Ставим прослушку на кнопку
        addButton = getActivity().findViewById(R.id.add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "В процессе разработки", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    // Задаем стартовые данные
    private void setInitialData() {
        breakdowns.add(new Breakdown("колесо пробил", "Toyota", "Supra", "12.12.12", "00:00", 0, 0));
        breakdowns.add(new Breakdown("колесо пробил", "Toyota", "Supra", "12.12.12", "00:00", 0, 0));
        breakdowns.add(new Breakdown("колесо пробил", "Toyota", "Supra", "12.12.12", "00:00", 0, 0));
        breakdowns.add(new Breakdown("колесо пробил", "Toyota", "Supra", "12.12.12", "00:00", 0, 0));
        breakdowns.add(new Breakdown("колесо пробил", "Toyota", "Supra", "12.12.12", "00:00", 0, 0));
        breakdowns.add(new Breakdown("колесо пробил", "Toyota", "Supra", "12.12.12", "00:00", 0, 0));
        breakdowns.add(new Breakdown("колесо пробил", "Toyota", "Supra", "12.12.12", "00:00", 0, 0));
        breakdowns.add(new Breakdown("колесо пробил", "Toyota", "Supra", "12.12.12", "00:00", 0, 0));
    }
}
