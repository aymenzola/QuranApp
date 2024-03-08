package com.app.dz.quranapp.Communs.Notification;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.app.dz.quranapp.R;
import com.app.dz.quranapp.MainActivity;


public class NotificationHelper extends ContextWrapper {
    public static final String channelID = "channelID";
    public static final String channelName = "Channel Name";

    private NotificationManager mManager;
    private String message;
    private String title;
    private int icon;

    public NotificationHelper(Context base,String message,String title) {
        super(base);
        this.message = message;
        this.title = title;
        Log.e("alarm", "notification class title "+title);
        this.icon = R.mipmap.icon_round;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
    }



    Intent intent= new Intent(this, MainActivity.class);
    PendingIntent contentIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_IMMUTABLE);
    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);

        getManager().createNotificationChannel(channel);


    }

    public NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return mManager;
    }

    public NotificationCompat.Builder getChannelNotification(String value) {

  /*      Intent intent2 = new Intent(this, multiplealarm.class);

// Loop counter `i` is used as a `requestCode`
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent2, 0);*/
        return new NotificationCompat.Builder(getApplicationContext(), channelID).
                setContentTitle(message).setContentText("حان الان موعد صلاة "+title)

                .setSmallIcon(icon)

                .setContentIntent(contentIntent)

                .setColor(getResources().getColor(R.color.orange));

        //notificationBuilder.setSmallIcon(R.mipmap.ic_appoint);// this is the white image with transparent background


    }


}