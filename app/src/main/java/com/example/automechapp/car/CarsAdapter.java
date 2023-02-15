package com.example.automechapp.car;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.automechapp.MainActivity;
import com.example.automechapp.R;

import java.util.List;

// Адаптер для авто
public class CarsAdapter extends RecyclerView.Adapter<CarsAdapter.CarViewHolder> {
    private final LayoutInflater inflater;
    // список авто
    private final List<Car> cars;

    public CarsAdapter(Context context, List<Car> cars) {
        this.inflater = LayoutInflater.from(context);
        this.cars = cars;
    }

    // В принципе, тут принцип тот же, что и в адаптере поломок
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
        holder.setUser_id(car.getUser_id());
        holder.carImage.setImageBitmap(car.getIcon());
    }

    @Override
    public int getItemCount() {
        return cars.size();
    }

    public static class CarViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener{
        final ImageView carImage;
        final TextView carNameView, manufactureView, modelView;
        private int id = -1;
        private int user_id = -1;

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
            // Отличие от того адаптера лишь тут
            // Запускаем активность авто
            Bundle bundle = new Bundle();
            bundle.putInt("id", id);
            bundle.putInt("user_id", user_id);

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

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }
    }
}
