package com.app.dz.quranapp.adhan;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.dz.quranapp.databinding.QuranActivityBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AdhanActivity extends AppCompatActivity {
    public static final int JOB_ID = 1001; // Unique ID for the job

    // ...

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        QuranActivityBinding binding = QuranActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.included.imgFilter.setOnClickListener(view -> schedulePrayerJob(getNextPrayerTimeDelay()));
        }
    private void schedulePrayerJob(long nextPrayerTimeMillis) {
        long currentTimeMillis = System.currentTimeMillis();
        long delayMillis = nextPrayerTimeMillis - currentTimeMillis;

        if (delayMillis > 0) {
            ComponentName serviceComponent = new ComponentName(this, PrayerJobIntentService.class);
            JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, serviceComponent)
                    .setMinimumLatency(delayMillis) // Set the delay until the job is scheduled
                    .setOverrideDeadline(delayMillis + 1000); // Set the maximum delay for the job

            JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
            jobScheduler.schedule(builder.build());
        } else {
            // The next prayer time is in the past, handle this case accordingly
        }
    }
    private long getNextPrayerTimeDelay() {
        // Implement logic to calculate the delay until the next prayer time
        // Return the delay in milliseconds
        // This will depend on your app's requirements and how you determine prayer times
        // Example: return System.currentTimeMillis() + 60 * 60 * 1000; // 1 hour from now

        Toast.makeText(this, "next time is "+convertMillisToTime(System.currentTimeMillis() + 5 * 60 * 1000), Toast.LENGTH_LONG).show();
        ;
        return System.currentTimeMillis() + 5 * 60 * 1000;
    }
    public static String convertMillisToTime(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date(millis));
    }
}
