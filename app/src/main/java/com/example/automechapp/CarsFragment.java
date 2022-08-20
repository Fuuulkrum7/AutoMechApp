package com.example.automechapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class CarsFragment extends Fragment {
    private final ArrayList<Car> cars = new ArrayList<Car>();
    public FloatingActionButton addButton;
    public RecyclerView carsView;

    @Nullable
    @Override
    public View onCreateView
            (@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler,
                container, false);


        setInitialData();

        carsView = view.findViewById(R.id.main_recycler);

        CarsAdapter adapter = new CarsAdapter(getContext(), cars);
        carsView.setAdapter(adapter);

        addButton = getActivity().findViewById(R.id.add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return view;
    }

    private void setInitialData() {
        cars.add(new Car("говновоз", "Toyota", "Supra", 0));
        cars.add(new Car("говновоз", "Toyota", "Supra", 0));
        cars.add(new Car("говновоз", "Toyota", "Supra", 0));
        cars.add(new Car("говновоз", "Toyota", "Supra", 0));
        cars.add(new Car("говновоз", "Toyota", "Supra", 0));
        cars.add(new Car("говновоз", "Toyota", "Supra", 0));
        cars.add(new Car("говновоз", "Toyota", "Supra", 0));
        cars.add(new Car("говновоз", "Toyota", "Supra", 0));
    }
}
