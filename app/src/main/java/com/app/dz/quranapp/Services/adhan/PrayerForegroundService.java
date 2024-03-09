package com.app.dz.quranapp.Services.adhan;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.app.dz.quranapp.R;

public class PrayerForegroundService extends Service {

    public static final String CHANNEL_ID = "PrayerChannel";
    public static final int NOTIFICATION_ID = 101; // Unique ID for the notification


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("testLog", "----> in foreground onStartCommand");
        if (intent != null) {
            String title = intent.getStringExtra("title");
            startForeground(NOTIFICATION_ID,createNotification(title));
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("testLog", "----> in foreground service onDestroy");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("testLog", "----> in foreground onCreate()");
        createNotificationChannel();
        Log.e("testLog", "----> we are creating notifcation channel "+CHANNEL_ID+" notif Id + "+NOTIFICATION_ID);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // Create a notification channel for Android Oreo and above
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Prayer Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    // Create a notification for the foreground service
    private Notification createNotification(String contentText) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.app_icon)
                .setContentTitle("Prayer App")
                .setContentText("الصلاة التالية "+contentText)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        return builder.build();
    }
}
