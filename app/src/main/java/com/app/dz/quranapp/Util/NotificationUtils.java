package com.app.dz.quranapp.Util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.app.dz.quranapp.R;
import com.app.dz.quranapp.Services.adhan.AdanAudioPlayerService;
import com.app.dz.quranapp.ui.activities.adhan.AdhanConfigActivity;

public class NotificationUtils {

    private static final String CHANNEL_ID_AdaHAN = "KounozChannel";
    private static final int NOTIFICATION_ID = 40; // Unique ID for the notification
    private static final int NOTIFICATION__ADKAR_ID = 50;

    public static void showPrayerNotification(Context context, String prayerTime,String prayerName) {
        createNotificationChannel(context);

        Intent notificationIntent = new Intent(context, AdhanConfigActivity.class); // Replace YourMainActivity with your actual main activity class
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);


        Intent stopIntent = new Intent(context, AdanAudioPlayerService.class);
        stopIntent.setAction(AdanAudioPlayerService.ACTION_STOP);
        PendingIntent stopPendingIntent = PendingIntent.getService(context, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID_AdaHAN)
                .setSmallIcon(R.drawable.ic_location_icon)
                .setContentTitle("حان الان موعد صلاة : "+prayerName)
                .setContentText("التوقيت " + prayerTime)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDeleteIntent(stopPendingIntent)
                .setAutoCancel(true)
                .setOngoing(false)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .addAction(0,"اغلاق الادان",stopPendingIntent); // Auto-cancel the notification when clicked

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            Log.e("testLog", "----> we are creating notifcation channel "+CHANNEL_ID_AdaHAN+" notif Id + "+NOTIFICATION_ID);
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }






    public static void showDikrNotification(Context context,String dickBody) {
        createNotificationChannel(context);

        Intent notificationIntent = new Intent(context, AdhanConfigActivity.class); // Replace YourMainActivity with your actual main activity class
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,notificationIntent,PendingIntent.FLAG_IMMUTABLE);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,CHANNEL_ID_AdaHAN)
                .setSmallIcon(R.drawable.ic_doaa)
                .setContentTitle("الادكار ")
                .setContentText(""+dickBody)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            Log.e("testLog", "----> we are creating notifcation "+CHANNEL_ID_AdaHAN+" notif Id + "+NOTIFICATION__ADKAR_ID);
            notificationManager.notify(NOTIFICATION__ADKAR_ID, builder.build());
        }
    }

    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID_AdaHAN,
                    "Kounoz Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );

            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}
