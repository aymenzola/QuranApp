package com.app.dz.quranapp.Services.adhan;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.app.dz.quranapp.Communs.PrayerTimesPreference;
import com.google.gson.Gson;
import com.app.dz.quranapp.Communs.PrayerTimesHelper;

public class AlarmBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Retrieve the prayerInfo from the intent
        String prayerInfoString = intent.getStringExtra("prayerInfo");
        PrayerTimesPreference.PrayerInfo prayerInfo = new Gson().fromJson(prayerInfoString, PrayerTimesPreference.PrayerInfo.class);

        // Perform your actions here using the prayerInfo
        // For example, you can log the prayer name
        Log.e("AlarmBroadcastReceiver", "Alarm triggered for prayer: " + prayerInfo.prayer_arabic);

        PrayerNotificationWorker.prepareNextPrayerTime(prayerInfo,context);
        // You can also schedule the next prayer alarm here
    }
}