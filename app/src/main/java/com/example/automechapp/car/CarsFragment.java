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

public class CarsFragment extends Fragment {

    private ArrayList<Car> cars = new ArrayList<>();
    public FloatingActionButton addButton;
    public RecyclerView carsView;

    private View loadingView;

    private int ownersCount = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_recycler, container, false);

        carsView = view.findViewById(R.id.main_recycler);
        loadingView = view.findViewById(R.id.main_loading);

        showLoading(true);

        // пустой адаптер сразу
        carsView.setAdapter(new CarsAdapter(getContext(), cars));

        loadCarsAsync();
        loadOwnersCountAsync();

        addButton = getActivity().findViewById(R.id.add);
        addButton.setOnClickListener(v -> {
            if (ownersCount > 0) {
                Intent intent = new Intent(getContext(), CarActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "Сначала добавьте хотя бы одного автовладельца", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void showLoading(boolean show) {
        if (loadingView == null || carsView == null) return;
        loadingView.setVisibility(show ? View.VISIBLE : View.GONE);
        carsView.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
    }

    private void loadCarsAsync() {
        new Thread(() -> {
            try {
                GetCars getCars = new GetCars(
                        getContext(),
                        null,
                        DatabaseInfo.STANDARD_DATE + " DESC"
                );

                // не плодим потоки: выполняем запрос в этом фоне
                getCars.run();
                ArrayList<Car> data = getCars.getData();

                new Handler(Looper.getMainLooper()).post(() -> {
                    if (!isAdded() || carsView == null) return;

                    cars = (data != null) ? data : new ArrayList<>();

                    CarsAdapter adapter = new CarsAdapter(requireContext(), cars);

                    // на следующий кадр, меньше микрофризов
                    carsView.post(() -> {
                        if (!isAdded() || carsView == null) return;
                        carsView.setAdapter(adapter);
                        showLoading(false);
                    });
                });

            } catch (Exception e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (!isAdded()) return;
                    showLoading(false);
                    Toast.makeText(getContext(), "Не удалось загрузить автомобили", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void loadOwnersCountAsync() {
        new Thread(() -> {
            try {
                GetOwners getOwners = new GetOwners(getContext(), null);
                getOwners.run();
                ownersCount = getOwners.getData().size();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
