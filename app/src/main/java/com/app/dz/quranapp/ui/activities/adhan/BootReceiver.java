package com.app.dz.quranapp.ui.activities.adhan;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;


import com.app.dz.quranapp.Communs.PrayerTimesHelper;
import com.app.dz.quranapp.Services.adhan.PrayerNotificationWorker;
import com.app.dz.quranapp.Util.SharedPreferenceManager;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class BootReceiver extends BroadcastReceiver {
    public static final String TAG_BROAD_CAST= "testKounozLog";
    Context context;
    public static final int JOB_ID = 1001; // Unique ID for the job

    @Override
    public void onReceive(Context contextt, Intent intent) {
        context = contextt;

        checkIfTheWorkIsScheduler(context);

        if (intent.getAction() != null) {
            switch (intent.getAction()) {
                case Intent.ACTION_BOOT_COMPLETED:
                case "android.intent.action.QUICKBOOT_POWERON":
                case "com.htc.intent.action.QUICKBOOT_POWERON":
                    Log.e(TAG_BROAD_CAST,"onReceive BOOT_COMPLETED or QUICKBOOT_POWERON");
                    // Handle boot completed actions
                    break;
                case "android.net.conn.CONNECTIVITY_CHANGE":
                    Log.e(TAG_BROAD_CAST,"onReceive CONNECTIVITY_CHANGE");
                    // Handle connectivity change actions
                    break;
                case Intent.ACTION_POWER_CONNECTED:
                    Log.e(TAG_BROAD_CAST,"onReceive ACTION_POWER_CONNECTED");
                    // Handle power connected actions
                    break;
                case "com.htc.intent.action.SCREEN_ON":
                    Log.e(TAG_BROAD_CAST,"onReceive SCREEN_ON");
                    // Handle screen on actions
                    break;
                case "com.htc.intent.action.TIME_TICK":
                    Log.e(TAG_BROAD_CAST,"onReceive TIME_TICK");
                    // Handle time tick actions
                    break;
                case "com.htc.intent.action.USER_PRESENT":
                    Log.e(TAG_BROAD_CAST,"onReceive USER_PRESENT");
                    // Handle user present actions
                    break;
                default:
                    Log.e(TAG_BROAD_CAST,"onReceive UNKNOWN_ACTION");
                    // Handle other actions
                    break;
            }
        }
    }

    private void checkIfTheWorkIsScheduler(Context context) {
        boolean alreadyExist = SharedPreferenceManager.getInstance(context).iSLocationAvialable();
        if (!alreadyExist) return;
        PrayerTimesHelper.getInstance(context).checkIfTheWorkIsScheduler(context);
    }



































    private boolean checkAndRestartJob(Context context) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (jobScheduler != null) {
            List<JobInfo> allPendingJobs = jobScheduler.getAllPendingJobs();
            for (JobInfo jobInfo : allPendingJobs) {
                if (jobInfo.getId() == JOB_ID) {
                    // The job is still scheduled, no need to reschedule
                    Log.e("testLog","onReceive The job is still scheduled, no need to reschedule ");
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