package com.example.automechapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class BreakdownsFragment extends Fragment {
    private final ArrayList<Breakdown> breakdowns = new ArrayList<Breakdown>();
    public FloatingActionButton addButton;
    public RecyclerView breakdownsView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_recycler,
                container, false);

        setInitialData();

        breakdownsView = view.findViewById(R.id.main_recycler);

        BreakdownsAdapter adapter = new BreakdownsAdapter(getContext(), breakdowns);
        breakdownsView.setAdapter(adapter);

        addButton = getActivity().findViewById(R.id.add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return view;
    }

    private void setInitialData() {
        breakdowns.add(new Breakdown("колесо пробил", "Toyota", "Supra", "12.12.12", "00:00", 0));
        breakdowns.add(new Breakdown("колесо пробил", "Toyota", "Supra", "12.12.12", "00:00", 0));
        breakdowns.add(new Breakdown("колесо пробил", "Toyota", "Supra", "12.12.12", "00:00", 0));
        breakdowns.add(new Breakdown("колесо пробил", "Toyota", "Supra", "12.12.12", "00:00", 0));
        breakdowns.add(new Breakdown("колесо пробил", "Toyota", "Supra", "12.12.12", "00:00", 0));
        breakdowns.add(new Breakdown("колесо пробил", "Toyota", "Supra", "12.12.12", "00:00", 0));
        breakdowns.add(new Breakdown("колесо пробил", "Toyota", "Supra", "12.12.12", "00:00", 0));
        breakdowns.add(new Breakdown("колесо пробил", "Toyota", "Supra", "12.12.12", "00:00", 0));
    }
}
