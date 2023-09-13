package com.example.automechapp.breakdown;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.automechapp.MainActivity;
import com.example.automechapp.R;
import com.example.automechapp.car.Car;
import com.example.automechapp.car.CarActivity;
import com.example.automechapp.database.DeleteBreakdowns;
import com.example.automechapp.database.DeleteCars;

import java.util.ArrayList;
import java.util.List;

// Адаптер для поломок
public class BreakdownsAdapter extends RecyclerView.Adapter<BreakdownsAdapter.BreakdownHolder> {
    private final LayoutInflater inflater;
    // список поломок
    private List<Breakdown> breakdowns;
    private int breakdown_id = -1;
    private int item_position;

    PopupMenu.OnMenuItemClickListener onMenuItemClickListener = item -> {
        switch (item.getItemId()) {
            // Если нажали на кнопку удалить
            case R.id.show_dustbin:
                if (breakdown_id == -1)
                    return false;
                ArrayList<Integer> arr = new ArrayList<>();
                arr.add(breakdown_id);

                DeleteBreakdowns deleteBreakdowns = new DeleteBreakdowns(MainActivity.getContext(), arr);
                deleteBreakdowns.start();

                breakdowns.remove(item_position);
                notifyItemRemoved(item_position);
                notifyItemRangeChanged(item_position, breakdowns.size());
                return true;
            case R.id.show_pencil:
                Toast.makeText(MainActivity.getContext(), "В разработке", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return false;
        }
    };

    public BreakdownsAdapter(Context context, List<Breakdown> breakdowns) {
        this.breakdowns = breakdowns;
        this.inflater = LayoutInflater.from(context);
    }

    // создаем холдер для поломки
    @NonNull
    @Override
    public BreakdownHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.breakdown_item, parent, false);
        return new BreakdownHolder(view);
    }

    // Добавляем данные из класса поломки
    @Override
    public void onBindViewHolder(@NonNull BreakdownHolder holder, int position) {
        Breakdown breakdown = breakdowns.get(position);

        holder.breakdownView.setText(breakdown.getBreakdown_name());
        holder.manufactureView.setText(breakdown.getManufacture());
        holder.modelView.setText(breakdown.getModel());
        holder.dateView.setText(breakdown.getDate());

        holder.breakdownImage.setImageBitmap(breakdown.getIcon());

        holder.setId(breakdown.getId());
        holder.setCarId(breakdown.getCar_id());

        holder.setPos(position);
    }

    // Число "поломок"
    @Override
    public int getItemCount() {
        return breakdowns.size();
    }

    // Сам холдер для пломки, в нем хранится инфа о поломке
    public class BreakdownHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener{
        final ImageView breakdownImage;
        final TextView breakdownView, manufactureView, modelView, dateView;
        private int id = -1, car_id = -1;
        private int pos;
        BreakdownHolder (View view){
            super(view);
            breakdownImage = view.findViewById(R.id.breakdown_image);
            breakdownView = view.findViewById(R.id.name_of_breakdown);
            manufactureView = view.findViewById(R.id.manufacturer_breakdown);
            modelView = view.findViewById(R.id.model_breakdown);
            dateView = view.findViewById(R.id.date_of_breakdown);

            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // Запуск активности с поломкой
            Bundle bundle = new Bundle();
            bundle.putInt("id", id);
            bundle.putInt("car_id", car_id);

            Intent intent = new Intent(MainActivity.getContext(), BreakdownActivity.class);

            intent.putExtras(bundle);
            MainActivity.getContext().startActivity(intent);
        }

        @Override
        public boolean onLongClick(View v) {
            PopupMenu popup = new PopupMenu(MainActivity.getContext(), v);
            popup.setOnMenuItemClickListener(onMenuItemClickListener);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.edit_menu, popup.getMenu());
            popup.show();

            item_position = pos;
            breakdown_id = id;
            return false;
        }

        private void setId(int id){
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public int getCarId() {
            return car_id;
        }

        public void setCarId(int car_id) {
            this.car_id = car_id;
        }

        public void setPos(int pos) {
            this.pos = pos;
        }
    }
}
