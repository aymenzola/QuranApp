package com.app.dz.quranapp.ui.activities.adhan;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;


import com.app.dz.quranapp.Services.adhan.PrayerNotificationWorker;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class BootReceiver extends BroadcastReceiver {
    Context context;
    public static final int JOB_ID = 1001; // Unique ID for the job

    @Override
    public void onReceive(Context contextt, Intent intent) {
        context = contextt;
        if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) ||
                intent.getAction().equals("android.intent.action.QUICKBOOT_POWERON")
                || intent.getAction().equals("com.htc.intent.action.QUICKBOOT_POWERON")) {
            // Schedule the job again when the device is rebooted
            Log.e("testLog", "onReceive schdule next job ");
            rescheduleForNextPrayerTime(5000);
            //checkAndRestartJob(context);
            //PrayerJobIntentService.schedulePrayerJob(System.currentTimeMillis() + 1000, context,true);
        } else if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)) {
            Log.e("testLog", "onReceive ACTION_POWER_CONNECTED");
            /* if (checkAndRestartJob(context)) {

            } else {
                //PrayerJobIntentService.schedulePrayerJob(System.currentTimeMillis() +1000, context,true);
            }*/
        }
    }

    private boolean checkAndRestartJob(Context context) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (jobScheduler != null) {
            List<JobInfo> allPendingJobs = jobScheduler.getAllPendingJobs();
            for (JobInfo jobInfo : allPendingJobs) {
                if (jobInfo.getId() == JOB_ID) {
                    // The job is still scheduled, no need to reschedule
                    Log.e("testLog", "onReceive The job is still scheduled, no need to reschedule ");
                    return true;
                }
            }
            // The job is not scheduled, reschedule or start a foreground service
            // You can add your logic here

            Log.e("testLog", "onReceive The job is not scheduled, reschedule or start a foreground service");
            return false;

        }
        return false;
    }


    private void
    rescheduleForNextPrayerTime(int nextDelay) {

        // Create a new OneTimeWorkRequest for the next prayer time
        OneTimeWorkRequest nextPrayerWork = new OneTimeWorkRequest.Builder(PrayerNotificationWorker.class)
                .setInitialDelay(nextDelay, TimeUnit.MILLISECONDS)
                .build();

        // Enqueue the new WorkRequest
        WorkManager.getInstance(context).enqueue(nextPrayerWork);
    }


}