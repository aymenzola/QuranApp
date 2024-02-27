package com.app.dz.quranapp.ui.activities.MainActivityPartes.TimeParte;

import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.app.dz.quranapp.data.room.Entities.DayPrayerTimes;
import com.app.dz.quranapp.Communs.Notification.NotificationHelper;
import com.app.dz.quranapp.Communs.Notification.OreoNotification;
import com.app.dz.quranapp.MainActivity;
import com.app.dz.quranapp.data.room.AppDatabase;
import com.app.dz.quranapp.data.room.Daos.DayPrayerTimesDao;
import com.app.dz.quranapp.data.room.DatabaseClient;

import java.util.Calendar;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AlarmReceiver extends BroadcastReceiver {
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onReceive(Context context, Intent intent) {
        //we will use vibrator first
        String title = intent.getStringExtra("title");
        Vibrator vibrator = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
        vibrator.vibrate(500);

        Log.e("alarm", "in receiver");
        Toast.makeText(context, "Alarm! Wake up! Wake up!", Toast.LENGTH_LONG).show();
        /*Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        Ringtone ringtone = RingtoneManager.getRingtone(context, alarmUri);
        ringtone.play();*/

        if (intent.getStringExtra("type").equals("salat")) schuduleNextAlarm(context);
        Log.e("alarm", "in receiver title "+title);
        manageNotitification(context,title);
    }

    private void schuduleNextAlarm(Context context) {
        AppDatabase db = DatabaseClient.getInstance(context).getAppDatabase();
        DayPrayerTimesDao dao = db.getDayPrayerTimesDao();

        //Today Calander
        Calendar ca = Calendar.getInstance();
        String date = ca.get(Calendar.DAY_OF_MONTH) + "-" + ca.get(Calendar.MONTH) + 1 + "-" + ca.get(Calendar.YEAR);

        //Tomorrow calendar
        Calendar nextCa = Calendar.getInstance();
        nextCa.add(Calendar.DAY_OF_MONTH, 1);
        String NextdateG = nextCa.get(Calendar.DAY_OF_MONTH) + "-" + nextCa.get(Calendar.MONTH) + 1 + "-" + nextCa.get(Calendar.YEAR);

        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            //Get Times from database For Today and Tomorrow
            DayPrayerTimes timesG = dao.getByDateWithoutObserver(date);
            DayPrayerTimes NextDaytimesG = dao.getByDateWithoutObserver(NextdateG);

            Log.e("alarm", "in receiver title "+NextDaytimesG.toString());

            //Today Prayer times
            PrayerTimes PrayerTimesToday = getConvertTimeMilliSeconds(timesG);

            //tommorow Prayer times
            long FajrTimeTommorow = getTommorowFajt(NextDaytimesG);

            findTheNextAlarm(context, PrayerTimesToday, FajrTimeTommorow);

        });


    }

    private void findTheNextAlarm(Context context, PrayerTimes PrayerTimesToday, long FajrTimeTommorow) {
        Calendar currant = Calendar.getInstance();

        if (currant.getTimeInMillis() > PrayerTimesToday.fajr && currant.getTimeInMillis() < PrayerTimesToday.duhr) {
            Log.e("alarm", "in type 1");
            setExactAlarm(context, PrayerTimesToday.duhr, "الظهر");
        } else if (currant.getTimeInMillis() > PrayerTimesToday.duhr && currant.getTimeInMillis() < PrayerTimesToday.assr) {
            Log.e("alarm", "in type 2");
            setExactAlarm(context, PrayerTimesToday.assr, "العصر");
        } else if (currant.getTimeInMillis() > PrayerTimesToday.assr && currant.getTimeInMillis() < PrayerTimesToday.maghrib) {
            Log.e("alarm", "in type 3");
            setExactAlarm(context, PrayerTimesToday.maghrib, "المغرب");
        } else if (currant.getTimeInMillis() > PrayerTimesToday.maghrib && currant.getTimeInMillis() < PrayerTimesToday.ishaa) {
            Log.e("alarm", "in type 4");
            setExactAlarm(context, PrayerTimesToday.ishaa, "العشاء");
        } else if (currant.getTimeInMillis() > PrayerTimesToday.ishaa && currant.getTimeInMillis() < FajrTimeTommorow) {
            Log.e("alarm", "in type 4");
            setExactAlarm(context, FajrTimeTommorow, "الفجر");
        } else if (currant.getTimeInMillis() < PrayerTimesToday.fajr) {
            Log.e("alarm", "in type 5");
            setExactAlarm(context, PrayerTimesToday.fajr, "الفجر");
        }
    }

    private long getTommorowFajt(DayPrayerTimes NextDayTimesG) {

        //tommorow fajr
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, getIntHour(NextDayTimesG.getFajr()));
        c.set(Calendar.MINUTE, getIntMinute(NextDayTimesG.getFajr()));
        return c.getTimeInMillis();
    }

    public PrayerTimes getConvertTimeMilliSeconds(DayPrayerTimes TimesG) {
        Log.e("alarm", " ConvertTimeMilliSeconds ");

        PrayerTimes prayerTimes = new PrayerTimes();
        Calendar calendar = Calendar.getInstance();

        //fajr
        calendar.set(Calendar.HOUR_OF_DAY, getIntHour(TimesG.getFajr()));
        calendar.set(Calendar.MINUTE, getIntMinute(TimesG.getFajr()));
        prayerTimes.fajr = calendar.getTimeInMillis();

        //thuhr
        calendar.set(Calendar.HOUR_OF_DAY, getIntHour(TimesG.getDhuhr()));
        calendar.set(Calendar.MINUTE, getIntMinute(TimesG.getDhuhr()));
        prayerTimes.duhr = calendar.getTimeInMillis();

        //assr
        calendar.set(Calendar.HOUR_OF_DAY, getIntHour(TimesG.getAsr()));
        calendar.set(Calendar.MINUTE, getIntMinute(TimesG.getAsr()));
        prayerTimes.assr = calendar.getTimeInMillis();

        //maghrib
        calendar.set(Calendar.HOUR_OF_DAY, getIntHour(TimesG.getMaghrib()));
        calendar.set(Calendar.MINUTE, getIntMinute(TimesG.getMaghrib()));
        prayerTimes.maghrib = calendar.getTimeInMillis();

        //ishaa
        calendar.set(Calendar.HOUR_OF_DAY, getIntHour(TimesG.getIsha()));
        calendar.set(Calendar.MINUTE, getIntMinute(TimesG.getIsha()));
        prayerTimes.ishaa = calendar.getTimeInMillis();


        return prayerTimes;
    }

    private void setExactAlarm(Context context, long time, String title) {
        Log.e("alarm", "setExactAlarm receive title "+title);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("title", title);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(time, pendingIntent), pendingIntent);
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        Log.e("alarm", "we set the next alarm at " + c.get(Calendar.HOUR_OF_DAY) + " : " + c.get(Calendar.MINUTE)+" day "+
                c.get(Calendar.DAY_OF_MONTH)+"-"+c.get(Calendar.MONTH)+1+"-"+c.get(Calendar.YEAR));
    }

    private void manageNotitification(Context context, String title) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {}
        sendsimpleNotification(context, "وقت الصلاة", title);
    }

    private void sendsimpleNotification(Context context, String message, String title) {
        NotificationHelper notificationHelper = new NotificationHelper(context, message, title);
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification("1");
        notificationHelper.getManager().notify(1, nb.build());

    }

    private void sendOreoNotification(Context context) {

        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 100, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        OreoNotification oreoNotification = new OreoNotification(context);
        Notification.Builder builder = oreoNotification.getOreoNotification("", "", pendingIntent, defaultSound);
        oreoNotification.getManager().notify(100, builder.build());

    }

    public Integer getIntHour(String time) {
        return Integer.parseInt(time.substring(0, 2));
    }

    public Integer getIntMinute(String time) {
        return Integer.parseInt(time.substring(3, 5));
    }
}