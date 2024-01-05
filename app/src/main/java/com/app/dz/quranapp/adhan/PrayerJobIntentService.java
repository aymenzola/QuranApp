package com.app.dz.quranapp.adhan;

import static com.app.dz.quranapp.adhan.PrayerForegroundService.CHANNEL_ID;
import static com.app.dz.quranapp.adhan.PrayerForegroundService.NOTIFICATION_ID;
import static com.app.dz.quranapp.adhan.AdhanActivity.JOB_ID;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;

import com.app.dz.quranapp.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PrayerJobIntentService extends JobService {


    public static long getNextPrayerTimeDelay() {
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

    public static void schedulePrayerJob(long nextPrayerTimeMillis,Context context) {
        long currentTimeMillis = System.currentTimeMillis();
        long delayMillis = nextPrayerTimeMillis - currentTimeMillis;

        if (delayMillis > 0) {
            ComponentName serviceComponent = new ComponentName(context, PrayerJobIntentService.class);
            JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, serviceComponent)
                    .setMinimumLatency(delayMillis) // Set the delay until the job is scheduled
                    .setOverrideDeadline(delayMillis + 1000); // Set the maximum delay for the job

            JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            jobScheduler.schedule(builder.build());
        } else {
            // The next prayer time is in the past, handle this case accordingly
        } }

    public static String convertMillisToTime(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date(millis));
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        // Perform background work here, e.g., send notification for prayer time

        // After handling the current prayer, schedule the next one
        long nextPrayerTimeDelayMillis = getNextPrayerTimeDelay();
        long nowPrayerTimeDelayMillis = System.currentTimeMillis();

        // Show a notification when the prayer time is reached
        NotificationUtils.showPrayerNotification(this, convertMillisToTime(nowPrayerTimeDelayMillis));

        // Start the foreground service
        Log.e("testLog", " startForegroundServiceIfNotExists ");
        startForegroundServiceIfNotExists();

        updateForegroundServiceNotification(nextPrayerTimeDelayMillis);
        schedulePrayerJob(nextPrayerTimeDelayMillis,this);
        Log.e("testLog","onStartJob");
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.e("testLog","onStopJob");
        return false;
    }

    private void startForegroundServiceIfNotExists() {
        if (!isNotificationActive(NOTIFICATION_ID)) {
            // Notification is not active, start the foreground service
            Log.e("testLog", "Starting foreground service");

            Intent serviceIntent = new Intent(this, PrayerForegroundService.class);
            startService(serviceIntent);

            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(new Intent(this, PrayerForegroundService.class));
            } else {
                startService(new Intent(this, PrayerForegroundService.class));
            }*/
        } else {
            Log.e("testLog", "the notification already exists ");
        }
    }

    private boolean isNotificationActive(int notificationId) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            // Get the list of active notifications and check if the specified ID exists
            StatusBarNotification[] activeNotifications = new StatusBarNotification[0];
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                activeNotifications = notificationManager.getActiveNotifications();
            }
            for (StatusBarNotification notification : activeNotifications) {
                if (notification.getId() == notificationId) {
                    return true; // Notification with the specified ID is active
                }
            }
        }
        return false; // Notification with the specified ID is not active
    }

}
