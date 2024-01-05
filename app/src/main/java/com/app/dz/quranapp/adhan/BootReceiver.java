package com.app.dz.quranapp.adhan;

import static com.app.dz.quranapp.adhan.PrayerJobIntentService.getNextPrayerTimeDelay;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) ||
        intent.getAction().equals("android.intent.action.QUICKBOOT_POWERON")
                || intent.getAction().equals("com.htc.intent.action.QUICKBOOT_POWERON")) {
            // Schedule the job again when the device is rebooted
            Log.e("testLog","onReceive schdule next job ");
            PrayerJobIntentService.schedulePrayerJob(System.currentTimeMillis() + 60 * 1000,context);
        }
    }


}