package com.example.automechapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewPagerHolder> {
    LayoutInflater inflater;
    private ArrayList<SliderItem> images;

    public ViewPagerAdapter(Context context, ArrayList<Bitmap> images) {
        this.inflater = LayoutInflater.from(context);
        setImages(images);
    }


    @NonNull
    @Override
    public ViewPagerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.car_photo, parent, false);

        return new ViewPagerAdapter.ViewPagerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewPagerHolder holder, int position) {
        holder.setImageView(images.get(position));
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public void setImages(ArrayList<Bitmap> images) {
        if (images == null) {
            return;
        }
        this.images = new ArrayList<SliderItem>();
        for (Bitmap b: images) {
            this.images.add(new SliderItem(b));
        }
    }

    public void addBitmap(Bitmap bitmap) {
        images.add(new SliderItem((bitmap)));
    }

    public static class ViewPagerHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;

        public ViewPagerHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.standard_photo);
        }

        void setImageView (SliderItem item) {
            imageView.setImageDrawable(item.getImage());
        }
    }
}
