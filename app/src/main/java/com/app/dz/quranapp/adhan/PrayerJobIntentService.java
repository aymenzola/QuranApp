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
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.util.Pair;

import androidx.core.app.NotificationCompat;

import com.app.dz.quranapp.Entities.DayPrayerTimes;
import com.app.dz.quranapp.MainFragmentsParte.TimeParte.PrayerTimes;
import com.app.dz.quranapp.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class PrayerJobIntentService extends JobService {

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.e("testLog", "onStartJob");

        long nowPrayerTimeDelayMillis = System.currentTimeMillis();
        NotificationUtils.showPrayerNotification(this,convertMillisToTime(nowPrayerTimeDelayMillis));
        Log.e("testLog", " startForegroundServiceIfNotExists ");
        startForegroundServiceIfNotExists();
        prepareAndScheduleNextPrayerTime(this);

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.e("testLog", "onStopJob");
        return false;
    }


    public void prepareAndScheduleNextPrayerTime(Context context) {
        PrayerTimesHelper prayerTimesHelper = new PrayerTimesHelper(new PrayerTimesHelper.TimesListener() {
            @Override
            public void onPrayerTimesResult(Map<String, DayPrayerTimes> TimesMap) {

            }

            @Override
            public void onPrayerNameAndTimeResult(Pair<String, Long> NameTimePair) {
                //todo real one long nextPrayerTimeDelayMillis = NameTimePair.second;
                long nextPrayerTimeDelayMillis = System.currentTimeMillis() + 5 * 60 * 1000;

                updateForegroundServiceNotification(NameTimePair.first,nextPrayerTimeDelayMillis);
                schedulePrayerJob(nextPrayerTimeDelayMillis,context);
            }

            @Override
            public void onError(String error) {

            }
        }, context);
        prayerTimesHelper.getDayPrayerTimes();
    }


    private void updateForegroundServiceNotification(String timeName,long nextPrayerTime) {
        // Create an intent to start the PrayerForegroundService
        Intent serviceIntent = new Intent(this, PrayerForegroundService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, serviceIntent, PendingIntent.FLAG_IMMUTABLE);

        // Create a notification for the foreground service with updated content
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_location_icon)
                .setContentTitle("Prayer App")
                .setContentText(timeName+" " + convertMillisToTime(nextPrayerTime))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Notification updatedNotification = builder.build();

        // Use startForeground to update the existing notification in the foreground service
        startForeground(NOTIFICATION_ID, updatedNotification);

    }

    public static void schedulePrayerJob(long nextPrayerTimeMillis, Context context) {
        long currentTimeMillis = System.currentTimeMillis();
        long delayMillis = nextPrayerTimeMillis - currentTimeMillis;

        if (delayMillis > 0) {
            ComponentName serviceComponent = new ComponentName(context, PrayerJobIntentService.class);
            JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, serviceComponent)
                    .setPersisted(true)
                    .setMinimumLatency(delayMillis) // Set the delay until the job is scheduled
                    .setOverrideDeadline(delayMillis + 1000); // Set the maximum delay for the job

            JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            jobScheduler.schedule(builder.build());
            Log.e("testLog", "schedulePrayerJob done at " + convertMillisToTime(nextPrayerTimeMillis));
        } else {
            // The next prayer time is in the past, handle this case accordingly
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("testLog", "Job service destroyed ");
    }

    public static String convertMillisToTime(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date(millis));
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
