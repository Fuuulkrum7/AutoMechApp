package com.example.automechapp.owner;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.automechapp.database.GetOwners;
import com.example.automechapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class OwnersFragment extends Fragment {
    private ArrayList<Owner> owners = new ArrayList<Owner>();
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

        addButton = getActivity().findViewById(R.id.add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), OwnerActivity.class);
                getContext().startActivity(intent);
            }
        });

        return view;
    }

    private void setInitialData() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                GetOwners getOwners = new GetOwners(getContext(), null);
                getOwners.start();

                try {
                    getOwners.join();
                    owners = getOwners.getOwnersList();

                    OwnersAdapter adapter = new OwnersAdapter(getContext(), owners);

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            ownersView.setAdapter(adapter);
                        }
                    });
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();
    }
}
