package com.app.dz.quranapp.Services.adhan;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.app.dz.quranapp.Communs.PrayerTimesPreference;
import com.app.dz.quranapp.R;
import com.google.gson.Gson;

public class AlarmBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Retrieve the prayerInfo from the intent
        String prayerInfoString = intent.getStringExtra("prayerInfo");
        if (prayerInfoString == null) {
            Log.e("AlarmBroadcastReceiver", "prayerInfo is null");
            showDownloadCompleteNotification("alarm called",context);
            return;
        }
        PrayerTimesPreference.PrayerInfo prayerInfo = new Gson().fromJson(prayerInfoString, PrayerTimesPreference.PrayerInfo.class);

        // Perform your actions here using the prayerInfo
        // For example, you can log the prayer name
        Log.e("AlarmBroadcastReceiver", "Alarm triggered for prayer: " + prayerInfo.prayer_arabic);

        PrayerNotificationWorker.prepareNextPrayerTime(prayerInfo,context);
        // You can also schedule the next prayer alarm here
    }

    public void showDownloadCompleteNotification(String message, Context context) {
        String channelId = "quran_download_channel1";
        String channelName = "Download Complete Notifications1";

        // Create a notification channel
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Notifications for download completion");
            notificationManager.createNotificationChannel(channel);
        }

        // Create a notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_adhan_active) // replace with your own icon
                .setContentTitle("Test Alarm")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH) // for heads-up notification
                .setAutoCancel(true); // notification will disappear after click

        builder.setDefaults(Notification.DEFAULT_ALL);

        // Show the notification
        notificationManager.notify(19, builder.build());
    }

}