package com.app.dz.quranapp.adhan;

import static com.app.dz.quranapp.adhan.PrayerForegroundService.CHANNEL_ID;
import static com.app.dz.quranapp.adhan.PrayerForegroundService.NOTIFICATION_ID;
import static com.app.dz.quranapp.adhan.AdhanActivity.JOB_ID;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;

import com.app.dz.quranapp.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PrayerJobIntentService extends JobIntentService {
    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        // Perform background work here, e.g., send notification for prayer time

        // After handling the current prayer, schedule the next one
        long nextPrayerTimeDelayMillis = getNextPrayerTimeDelay();

        // Show a notification when the prayer time is reached
        NotificationUtils.showPrayerNotification(this, convertMillisToTime(nextPrayerTimeDelayMillis));

        updateForegroundServiceNotification(nextPrayerTimeDelayMillis);
        schedulePrayerJob(nextPrayerTimeDelayMillis);

    }

    private long getNextPrayerTimeDelay() {
        // Implement logic to calculate the delay until the next prayer time
        // Return the delay in milliseconds
        // This will depend on your app's requirements and how you determine prayer times
        // Example: return System.currentTimeMillis() + 60 * 60 * 1000; // 1 hour from now

        return System.currentTimeMillis() + 5 * 60 * 1000;
    }



    private void updateForegroundServiceNotification(long nextPrayerTime) {
        // Create an intent to start the PrayerForegroundService
        Intent serviceIntent = new Intent(this, PrayerForegroundService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, serviceIntent, PendingIntent.FLAG_IMMUTABLE);

        // Create a notification for the foreground service with updated content
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_location_icon)
                .setContentTitle("Prayer App")
                .setContentText("Next Prayer Time: " + convertMillisToTime(nextPrayerTime))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Notification updatedNotification = builder.build();

        // Use startForeground to update the existing notification in the foreground service
        startForeground(NOTIFICATION_ID, updatedNotification);
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
        } }

    public static String convertMillisToTime(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date(millis));
    }
}
