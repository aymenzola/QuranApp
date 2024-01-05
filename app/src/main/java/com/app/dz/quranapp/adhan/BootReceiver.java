package com.app.dz.quranapp.adhan;


import static com.app.dz.quranapp.adhan.AdhanActivity.JOB_ID;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) ||
                intent.getAction().equals("android.intent.action.QUICKBOOT_POWERON")
                || intent.getAction().equals("com.htc.intent.action.QUICKBOOT_POWERON")) {
            // Schedule the job again when the device is rebooted
            Log.e("testLog", "onReceive schdule next job ");
            PrayerJobIntentService.schedulePrayerJob(System.currentTimeMillis() + 60 * 1000, context,true);
            checkAndRestartJob(context);
        } else if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)) {
            Log.e("testLog", "onReceive ACTION_POWER_CONNECTED");
            if (checkAndRestartJob(context)) {

            } else {
                PrayerJobIntentService.schedulePrayerJob(System.currentTimeMillis() + 60 * 1000, context,true);
            }
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


}