package com.example.automechapp.car;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.automechapp.R;
import com.example.automechapp.owner.Owner;

import java.util.ArrayList;

public class CarSpinnerAdapter extends ArrayAdapter<Car> {
    ArrayList<Car> s;

    public CarSpinnerAdapter(Context context, int textViewResourceId,
                               ArrayList<Car> objects) {
        super(context, textViewResourceId, objects);
        s = objects;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                @NonNull ViewGroup parent) {

        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView,
                              ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View row = inflater.inflate(R.layout.spinner_dropdown_item, parent, false);
        TextView label = row.findViewById(R.id.username_spinner);
        label.setText(s.get(position).getCarText());

        return row;
    }
}
