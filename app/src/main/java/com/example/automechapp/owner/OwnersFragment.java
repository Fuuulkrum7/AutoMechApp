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
    private ArrayList<Owner> owners = new ArrayList<>();
    public FloatingActionButton addButton;
    public RecyclerView ownersView;

    private View loading;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_recycler, container, false);

        ownersView = view.findViewById(R.id.main_recycler);
        loading = view.findViewById(R.id.main_loading);

        // пустой адаптер сразу
        ownersView.setAdapter(new OwnersAdapter(getContext(), owners));

        setInitialData();

        addButton = getActivity().findViewById(R.id.add);
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), OwnerActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void showLoading(boolean show) {
        if (loading == null || ownersView == null) return;
        loading.setVisibility(show ? View.VISIBLE : View.GONE);
        ownersView.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
    }

    private void setInitialData() {
        showLoading(true);

        new Thread(() -> {
            try {
                GetOwners getOwners = new GetOwners(getContext(), null);
                getOwners.run();
                ArrayList<Owner> data = getOwners.getData();

                new Handler(Looper.getMainLooper()).post(() -> {
                    if (!isAdded() || ownersView == null) return;

                    owners = (data != null) ? data : new ArrayList<>();

                    OwnersAdapter adapter = new OwnersAdapter(requireContext(), owners);

                    ownersView.post(() -> {
                        if (!isAdded() || ownersView == null) return;
                        ownersView.setAdapter(adapter);
                        showLoading(false);
                    });
                });
            } catch (Exception e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (!isAdded()) return;
                    showLoading(false);
                });
            }
        }).start();
    }
}
