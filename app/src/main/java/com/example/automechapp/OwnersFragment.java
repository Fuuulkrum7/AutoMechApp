package com.example.automechapp;

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

public class OwnersFragment extends Fragment {
    private final ArrayList<Owner> owners = new ArrayList<Owner>();
    public FloatingActionButton addButton;
    public RecyclerView ownersView;

    @Nullable
    @Override
    public View onCreateView
            (@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler,
                container, false);

        ownersView = view.findViewById(R.id.main_recycler);

        setInitialData();

        OwnersAdapter adapter = new OwnersAdapter(getContext(), owners);
        ownersView.setAdapter(adapter);

        addButton = getActivity().findViewById(R.id.add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return view;
    }

    private void setInitialData() {
        owners.add(new Owner(0,"Михаил", "Алексеевич", "Гершкович", "04.10.2004", "01000000", "Челябинск", "Челябинск", "b1", 1010, 101010));
        owners.add(new Owner(0,"Михаил", "Алексеевич", "Гершкович", "04.10.2004", "01000000", "Челябинск", "Челябинск", "b1", 1010, 101010));
        owners.add(new Owner(0,"Михаил", "Алексеевич", "Гершкович", "04.10.2004", "01000000", "Челябинск", "Челябинск", "b1", 1010, 101010));
        owners.add(new Owner(0,"Михаил", "Алексеевич", "Гершкович", "04.10.2004", "01000000", "Челябинск", "Челябинск", "b1", 1010, 101010));
        owners.add(new Owner(0,"Михаил", "Алексеевич", "Гершкович", "04.10.2004", "01000000", "Челябинск", "Челябинск", "b1", 1010, 101010));
        owners.add(new Owner(0,"Михаил", "Алексеевич", "Гершкович", "04.10.2004", "01000000", "Челябинск", "Челябинск", "b1", 1010, 101010));
    }
}
