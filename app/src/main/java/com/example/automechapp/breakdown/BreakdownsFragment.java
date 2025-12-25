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
import com.example.automechapp.car.Car;
import com.example.automechapp.database.DatabaseInfo;
import com.example.automechapp.database.GetBreakdowns;
import com.example.automechapp.database.GetCars;
import com.example.automechapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class BreakdownsFragment extends Fragment {

    private ArrayList<Breakdown> breakdowns = new ArrayList<>();
    private RecyclerView breakdownsView;
    private View loadingView;

    public FloatingActionButton addButton;

    private int carsCount = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_recycler, container, false);

        breakdownsView = view.findViewById(R.id.main_recycler);
        loadingView = view.findViewById(R.id.main_loading);

        showLoading(true);

        breakdownsView.setAdapter(new BreakdownsAdapter(getContext(), breakdowns));

        loadBreakdownsAsync();
        loadCarsCountAsync();

        addButton = getActivity().findViewById(R.id.add);
        addButton.setOnClickListener(v -> {
            if (carsCount > 0) {
                Intent intent = new Intent(getContext(), BreakdownActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(v.getContext(), "Сначала добавьте автомобиль", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void showLoading(boolean show) {
        if (loadingView == null || breakdownsView == null) return;
        loadingView.setVisibility(show ? View.VISIBLE : View.GONE);
        breakdownsView.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
    }

    private void loadBreakdownsAsync() {
        try {
            GetBreakdowns getBreakdowns = new GetBreakdowns(
                    getContext(),
                    null,
                    DatabaseInfo.EDIT_TIME + " DESC"
            );
            getBreakdowns.setRunnable(() -> {
                ArrayList<Breakdown> data = getBreakdowns.getData();
                if (!isAdded() || breakdownsView == null) return;

                breakdowns = (data != null) ? data : new ArrayList<>();

                BreakdownsAdapter adapter = new BreakdownsAdapter(requireContext(), breakdowns);

                breakdownsView.post(() -> {
                    if (!isAdded() || breakdownsView == null) return;
                    breakdownsView.setAdapter(adapter);
                    showLoading(false);
                });
            });
            getBreakdowns.start();
        } catch (Exception e) {
            e.printStackTrace();
            new Handler(Looper.getMainLooper()).post(() -> {
                if (!isAdded()) return;
                showLoading(false);
                Toast.makeText(getContext(), "Не удалось загрузить поломки", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void loadCarsCountAsync() {
        try {
            GetCars getCars = new GetCars(
                    getContext(),
                    null,
                    DatabaseInfo.STANDARD_DATE + " DESC"
            );
            getCars.setRunnable(() -> {
                ArrayList<Car> cars = getCars.getData();
                carsCount = (cars == null) ? 0 : cars.size();
            });
            getCars.start();

        } catch (Exception e) {
            e.printStackTrace();
            new Handler(Looper.getMainLooper()).post(() -> {
                if (!isAdded()) return;
                Toast.makeText(getContext(), "Не удалось проверить авто", Toast.LENGTH_SHORT).show();
            });
        }
    }
}
