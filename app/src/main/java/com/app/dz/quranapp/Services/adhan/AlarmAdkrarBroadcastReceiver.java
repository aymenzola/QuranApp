package com.app.dz.quranapp.Services.adhan;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.app.dz.quranapp.Communs.PrayerTimesPreference;
import com.app.dz.quranapp.Util.NotificationUtils;
import com.app.dz.quranapp.ui.activities.AdkarParte.AdkarCountsHelper;
import com.google.gson.Gson;

public class AlarmAdkrarBroadcastReceiver extends BroadcastReceiver {

    AdkarCountsHelper SM;
    @Override
    public void onReceive(Context context, Intent intent) {
        SM = AdkarCountsHelper.getInstance(context);
        SM.scheduleDikrAlarm(context);
        NotificationUtils.showDikrNotification(context,SM.getDikrDependOnCurrentTime());
    }
}
