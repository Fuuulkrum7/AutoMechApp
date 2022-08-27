package com.example.automechapp;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class CarsFragment extends Fragment {
    private ArrayList<Car> cars = new ArrayList<Car>();
    public FloatingActionButton addButton;
    public RecyclerView carsView;

    @Nullable
    @Override
    public View onCreateView
            (@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler,
                container, false);

        carsView = view.findViewById(R.id.main_recycler);

        Thread thread = new Thread() {
            @Override
            public void run() {
                GetCars getCars = new GetCars(
                        getContext(),
                        new String[]{
                            DatabaseInfo.CAR_NAME,
                            DatabaseInfo.CAR_MANUFACTURE,
                            DatabaseInfo.CAR_MODEL,
                            DatabaseInfo.CAR_ID
                        },
                        null,
                        null
                        );
                try {
                    getCars.join();
                    cars = getCars.getData();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

                setInitialData();

                CarsAdapter adapter = new CarsAdapter(getContext(), cars);
                carsView.setAdapter(adapter);
            }
        };

        thread.start();

        addButton = getActivity().findViewById(R.id.add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return view;
    }

    private void setInitialData() {
        cars.add(new Car("говновоз", "Toyota", "Supra", 0, null));
        cars.add(new Car("говновоз", "Toyota", "Supra", 0, null));
        cars.add(new Car("говновоз", "Toyota", "Supra", 0, null));
        cars.add(new Car("говновоз", "Toyota", "Supra", 0, null));
        cars.add(new Car("говновоз", "Toyota", "Supra", 0, null));
        cars.add(new Car("говновоз", "Toyota", "Supra", 0, null));
        cars.add(new Car("говновоз", "Toyota", "Supra", 0, null));
        cars.add(new Car("говновоз", "Toyota", "Supra", 0, null));
    }
}
