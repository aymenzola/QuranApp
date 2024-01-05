package com.app.dz.quranapp.adhan;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.dz.quranapp.Entities.DayPrayerTimes;
import com.app.dz.quranapp.databinding.QuranActivityBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class AdhanActivity extends AppCompatActivity {
    public static final int JOB_ID = 1001; // Unique ID for the job
    public static final int JOB_IDtest2= 1001; // Unique ID for the job

    // ...

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        QuranActivityBinding binding = QuranActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.included.imgFilter.setOnClickListener(view -> schedulePrayerJob());
        }
    private void schedulePrayerJob() {
        PrayerJobIntentService.schedulePrayerJob(System.currentTimeMillis() + 5000,AdhanActivity.this,true);
    }
    private long getNextPrayerTimeDelay() {
        return System.currentTimeMillis() + 5 * 60 * 1000;
    }
    public static String convertMillisToTime(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date(millis));
    }
}
