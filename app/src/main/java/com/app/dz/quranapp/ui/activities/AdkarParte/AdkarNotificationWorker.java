package com.app.dz.quranapp.ui.activities.AdkarParte;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.app.dz.quranapp.Util.NotificationUtils;

import java.util.Calendar;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class AdkarNotificationWorker extends Worker {

    AdkarCountsHelper SM;

    public AdkarNotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        SM = AdkarCountsHelper.getInstance(getApplicationContext());
    }

    @NonNull
    @Override
    public Result doWork() {

        if (SM.getIsFirstNotification()) {
            Log.e("testLog", "this is the first notification");
            SM.setIsFirstNotification(false);
            return Result.success();
        }
        int notificationCount = SM.getAlreadyAppearedNotificationCount();

        //check if this is the last notification by comparing the count with the scheduled notification count
        if (notificationCount >= SM.getScheduledNotificationCount()) {
            fireLastNotification();
        } else {
            showDikrNotification();
        }

        return Result.success();
    }

    private void fireLastNotification() {
        showDikrNotification();
        cancelPeriodicWorkNotification();
        scheduleTomorrowNotification();
    }

    private void cancelPeriodicWorkNotification() {
        String workId = SM.getAdkarWorkId();
        if (workId != null) {
            WorkManager.getInstance(getApplicationContext()).cancelWorkById(UUID.fromString(workId));
        }
    }

    private void scheduleTomorrowNotification() {
        SM.resetAlreadyAppearedNotificationCount();

        Calendar now = Calendar.getInstance();

        // Get the current hour and minute
        int currentHour = now.get(Calendar.HOUR_OF_DAY);
        int currentMinute = now.get(Calendar.MINUTE);

        long interval;
        if (currentHour >= 8) {
            // Calculate the remaining minutes today
            int remainingMinutesToday = (24 * 60) - (currentHour * 60 + currentMinute);

            // Add 8 hours (for 08:00 AM of the next day)
            int totalMinutesUntilNextDayAt8AM = remainingMinutesToday + (8 * 60);

            // Convert the total minutes to hours
            interval = totalMinutesUntilNextDayAt8AM / 60;
        } else {
            // Calculate the remaining minutes today
            int remainingMinutesToday = (8 * 60) - (currentHour * 60 + currentMinute);

            // Convert the total minutes to hours
            interval = remainingMinutesToday / 60;
        }


        //schedule the next day notification
        OneTimeWorkRequest adkarNotificationRequest = new OneTimeWorkRequest.Builder(AdkarNotificationWorkerTomorrow.class)
                .setInitialDelay(interval, TimeUnit.HOURS).build();
        SM.saveAdkarWorkId(adkarNotificationRequest.getId().toString());

        WorkManager.getInstance(getApplicationContext()).enqueue(adkarNotificationRequest);
    }

    private void showDikrNotification() {
        SM.incrementAlreadyAppearedNotificationCount();
        String dikr = SM.getDikrDependOnCurrentTime();
        NotificationUtils.showDikrNotification(getApplicationContext(), dikr);
    }


}

