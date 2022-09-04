package com.example.automechapp;

import android.annotation.SuppressLint;
import android.app.AutomaticZenRule;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OwnersAdapter extends RecyclerView.Adapter<OwnersAdapter.OwnerViewHolder>{
    private final LayoutInflater inflater;
    private final List<Owner> owners;

    public OwnersAdapter(Context context, List<Owner> owners) {
        this.inflater = LayoutInflater.from(context);
        this.owners = owners;
    }

    @NonNull
    @Override
    public OwnerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.owner_item, parent, false);

        return new OwnerViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull OwnerViewHolder holder, int position) {
        Owner owner = owners.get(position);
        holder.usernameView.setText(owner.getUsername());
        holder.patronymicView.setText(owner.getPatronymic());
        holder.surnameView.setText(owner.getSurname());
        holder.dobView.setText(owner.getDob());
        holder.regionView.setText(owner.getRegion());
        holder.driverLicenseView.setText(Integer.toString(owner.getDriver_license()));
        holder.issuingRegionView.setText(owner.getIssuing_region());
        holder.categoriesView.setText(owner.getCategories());
        holder.passportSeriesView.setText(Integer.toString(owner.getPassport_series()));
        holder.passportNumberView.setText(Integer.toString(owner.getPassport_number()));

        holder.disableTexts();
    }

    @Override
    public int getItemCount() {
        return owners.size();
    }

    public static class OwnerViewHolder extends RecyclerView.ViewHolder {
        EditText usernameView, patronymicView, surnameView, dobView, regionView, driverLicenseView, issuingRegionView, categoriesView, passportSeriesView, passportNumberView;
        public OwnerViewHolder(View view) {
            super(view);
            usernameView = view.findViewById(R.id.username);
            patronymicView = view.findViewById(R.id.patronymic);
            surnameView = view.findViewById(R.id.surname);
            dobView = view.findViewById(R.id.date_of_birth);
            regionView = view.findViewById(R.id.region);
            driverLicenseView = view.findViewById(R.id.driver_license);
            issuingRegionView = view.findViewById(R.id.issung_region);
            categoriesView = view.findViewById(R.id.categories);
            passportSeriesView = view.findViewById(R.id.passport_series);
            passportNumberView = view.findViewById(R.id.passport_number);
        }

        public void disableTexts() {
            usernameView.setEnabled(false);
            patronymicView.setEnabled(false);
            surnameView.setEnabled(false);
            dobView.setEnabled(false);
            regionView.setEnabled(false);
            driverLicenseView.setEnabled(false);
            issuingRegionView.setEnabled(false);
            categoriesView.setEnabled(false);
            passportSeriesView.setEnabled(false);
            passportNumberView.setEnabled(false);
        }
    }
}
