package com.example.automechapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CarsAdapter extends RecyclerView.Adapter<CarsAdapter.CarViewHolder> {
    private final LayoutInflater inflater;
    private final List<Car> cars;

    public CarsAdapter(Context context, List<Car> cars) {
        this.inflater = LayoutInflater.from(context);
        this.cars = cars;
    }

    @NonNull
    @Override
    public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.car_item, parent, false);

        return new CarsAdapter.CarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarViewHolder holder, int position) {
        Car car = cars.get(position);
        holder.carNameView.setText(car.getCarName());
        holder.manufactureView.setText(car.getManufacture());
        holder.modelView.setText(car.getModel());
        holder.setId(car.getId());
    }

    @Override
    public int getItemCount() {
        return cars.size();
    }

    public static class CarViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener{
        final ImageView carImage;
        final TextView carNameView, manufactureView, modelView;
        private int id = -1;

        CarViewHolder (View view) {
            super(view);
            carImage = view.findViewById(R.id.car_photo);
            carNameView = view.findViewById(R.id.car_name);
            manufactureView = view.findViewById(R.id.car_manufacture);
            modelView = view.findViewById(R.id.car_model);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(v.getContext(), "В процессе разработки", Toast.LENGTH_SHORT).show();

            Bundle bundle = new Bundle();
            bundle.putInt("id", id);

            Intent intent = new Intent(MainActivity.getContext(), CarActivity.class);

            intent.putExtras(bundle);
            MainActivity.getContext().startActivity(intent);
        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }

        private void setId(int id){
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }
}
