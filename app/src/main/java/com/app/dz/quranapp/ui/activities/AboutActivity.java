package com.app.dz.quranapp.ui.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.app.dz.quranapp.R;
import com.app.dz.quranapp.databinding.ActivityAboutBinding;
import com.bumptech.glide.Glide;

@SuppressLint("CustomSplashScreen")
public class AboutActivity extends AppCompatActivity {

    ActivityAboutBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getColor(R.color.white));
        }
        Glide.with(this).load(R.drawable.app_icon).into(binding.imageview);
        binding.imgClose.setOnClickListener(v -> onBackPressed());
        binding.tvShare.setOnClickListener(v -> shareApp());

        displayMessage();

    }

    private void displayMessage() {
        // create a new SpannableStringBuilder
        SpannableStringBuilder builder = new SpannableStringBuilder(getString(R.string.my_string));

// set color and bold for the first part
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(getResources().getColor(R.color.communBrown));
        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
        builder.setSpan(colorSpan, 103, 151, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

// set color and bold for the second part
        colorSpan = new ForegroundColorSpan(getResources().getColor(R.color.communBrown));
        boldSpan = new StyleSpan(Typeface.BOLD);
        builder.setSpan(colorSpan, 221, 308, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

// set color and bold for the third part
        colorSpan = new ForegroundColorSpan(getResources().getColor(R.color.communBrown));
        boldSpan = new StyleSpan(Typeface.BOLD);
        builder.setSpan(colorSpan, 352, 419, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

// set color and bold for the fourth part
        colorSpan = new ForegroundColorSpan(getResources().getColor(R.color.communBrown));
        boldSpan = new StyleSpan(Typeface.BOLD);
        builder.setSpan(colorSpan, 606, 617, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(boldSpan, 606, 617, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        colorSpan = new ForegroundColorSpan(getResources().getColor(R.color.communBrown));
        boldSpan = new StyleSpan(Typeface.BOLD);
        builder.setSpan(colorSpan, 638, 651, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(boldSpan, 638, 651, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

// set the updated text on the TextView
        binding.tvMessage.setText(builder);
    }

    private void shareApp() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String shareMessage = "Check out this amazing app: https://play.google.com/store/apps/details?id=com.app.dz.quranapp";
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
        startActivity(Intent.createChooser(shareIntent, "مشاركة التطبيق"));
    }

}