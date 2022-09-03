package com.example.automechapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// Адаптер для поломок
public class BreakdownsAdapter extends RecyclerView.Adapter<BreakdownsAdapter.BreakdownHolder> {
    private final LayoutInflater inflater;
    // список поломок
    private final List<Breakdown> breakdowns;

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
    }

    // Число "поломок"
    @Override
    public int getItemCount() {
        return breakdowns.size();
    }

    // Сам холдер для пломки, в нем хранится инфа о поломке
    public static class BreakdownHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        final ImageView breakdownImage;
        final TextView breakdownView, manufactureView, modelView, dateView;
        BreakdownHolder (View view){
            super(view);
            breakdownImage = view.findViewById(R.id.breakdown_image);
            breakdownView = view.findViewById(R.id.breakdown_name);
            manufactureView = view.findViewById(R.id.breakdown_manufacturer);
            modelView = view.findViewById(R.id.breakdown_model);
            dateView = view.findViewById(R.id.breakdown_date);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(v.getContext(), "В процессе разработки", Toast.LENGTH_SHORT).show();
        }
    }
}
