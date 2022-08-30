package com.example.automechapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
                            DatabaseInfo.CAR_ID,
                            DatabaseInfo.CAR_PHOTO,
                            DatabaseInfo.OWNER_ID
                        },
                        null,
                        DatabaseInfo.STANDARD_DATE + " DESC"
                        );
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
                CarsAdapter adapter = new CarsAdapter(MainActivity.getContext(), cars);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        carsView.setAdapter(adapter);
                    }
                });

            }
        };

        thread.start();

        addButton = getActivity().findViewById(R.id.add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.getContext(), CarActivity.class);
                MainActivity.getContext().startActivity(intent);
            }
        });

        return view;
    }

    private void setInitialData() {
        cars.add(new Car("говновоз", "Toyota", "Supra", 0, null, 0));
        cars.add(new Car("говновоз", "Toyota", "Supra", 0, null, 0));
        cars.add(new Car("говновоз", "Toyota", "Supra", 0, null, 0));
        cars.add(new Car("говновоз", "Toyota", "Supra", 0, null, 0));
        cars.add(new Car("говновоз", "Toyota", "Supra", 0, null, 0));
        cars.add(new Car("говновоз", "Toyota", "Supra", 0, null, 0));
    }
}
