package com.example.automechapp;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class SliderItem {
    private Drawable image;

    SliderItem(Bitmap image) {
        this.image = new BitmapDrawable(image);
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }
}
