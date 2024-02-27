package com.app.dz.quranapp.fix_new_futers.ai_commands;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.app.dz.quranapp.R;

public class MyWidgetProvider extends AppWidgetProvider {
    private static final String ACTION_CLICK = "ACTION_CLICK";

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Update each widget instance
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);


            Intent serviceIntent = new Intent(context, AudioForegroundService.class);
            serviceIntent.setAction("START_RECOGNITION");
            PendingIntent pendingIntent = PendingIntent.getService(context, 0, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            // Assign the pending intent to the button in your widget layout
            views.setOnClickPendingIntent(R.id.image_start_listening, pendingIntent);

            // Set up the click event for the button
           /* Intent intent = new Intent(context,MyWidgetProvider.class);
            intent.setAction(ACTION_CLICK);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.startRecognizerButton, pendingIntent);
*/
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        /*
        if (intent.getAction().equals(ACTION_CLICK)) {
            // Handle button click here
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            remoteViews.setTextViewText(R.id.startRecognizerButton, "Speak now");

            // Update the widget
            AppWidgetManager.getInstance(context).updateAppWidget(new ComponentName(context, MyWidgetProvider.class), remoteViews);
        }*/
    }
}
