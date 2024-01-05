package com.app.dz.quranapp.adhan;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.app.dz.quranapp.R;

public class NotificationUtils {

    private static final String CHANNEL_ID_AdaHAN = "AdhanChannel";
    private static final int NOTIFICATION_ID = 40; // Unique ID for the notification

    public static void showPrayerNotification(Context context, String prayerTime) {
        createNotificationChannel(context);

        Intent notificationIntent = new Intent(context, AdhanActivity.class); // Replace YourMainActivity with your actual main activity class
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID_AdaHAN)
                .setSmallIcon(R.drawable.ic_location_icon)
                .setContentTitle("adhan Prayer Time!")
                .setContentText("It's time for " + prayerTime)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true); // Auto-cancel the notification when clicked

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }

    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID_AdaHAN,
                    "Prayer adan Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );

            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}
