package com.app.dz.quranapp.ui.activities.MainActivityPartes.TimeParte;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

public class MarginPageTransformer implements ViewPager2.PageTransformer {
    private int margin;

    public MarginPageTransformer(int margin) {
        this.margin = margin;
    }

    @Override
    public void transformPage(@NonNull View page, float position) {
        if (position != 0) {
            page.setTranslationX(-margin * position);
        } else if (position == 0) {
            page.setTranslationX(0);
        }
    }
}