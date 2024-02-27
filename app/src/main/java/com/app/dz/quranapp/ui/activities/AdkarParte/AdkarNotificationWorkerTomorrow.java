package com.app.dz.quranapp.ui.activities.AdkarParte;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.app.dz.quranapp.Util.NotificationUtils;

import java.util.concurrent.TimeUnit;

public class AdkarNotificationWorkerTomorrow extends Worker {
    AdkarCountsHelper AdkarHelper;
    Context context;

    public AdkarNotificationWorkerTomorrow(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
        AdkarHelper = AdkarCountsHelper.getInstance(context);
    }

    @NonNull
    @Override
    public Result doWork() {
        scheduleAdkarForTodayAdkar();
        showFirstNotification();
        return Result.success();
    }

    private void showFirstNotification() {
        String dikr = AdkarHelper.getDikrDependOnCurrentTime();
        NotificationUtils.showDikrNotification(getApplicationContext(),dikr);
    }

    private void scheduleAdkarForTodayAdkar() {
        //AdkarHelper.setIsFirstNotification(true);
        PeriodicWorkRequest adkarNotificationRequest = new PeriodicWorkRequest.Builder(AdkarNotificationWorker.class,AdkarHelper.getAdkarLevel(),TimeUnit.HOURS).build();
        AdkarHelper.saveAdkarWorkId(adkarNotificationRequest.getId().toString());
        AdkarHelper.saveScheduledNotificationCount((int) AdkarHelper.getNumberOfNotifications());
        WorkManager.getInstance(context).enqueue(adkarNotificationRequest);
    }

}

