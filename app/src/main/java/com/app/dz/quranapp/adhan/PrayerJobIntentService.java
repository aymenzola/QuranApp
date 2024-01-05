package com.app.dz.quranapp.adhan;

import static com.app.dz.quranapp.adhan.PrayerForegroundService.CHANNEL_ID;
import static com.app.dz.quranapp.adhan.PrayerForegroundService.NOTIFICATION_ID;
import static com.app.dz.quranapp.adhan.AdhanActivity.JOB_ID;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.util.Pair;

import androidx.core.app.NotificationCompat;

import com.app.dz.quranapp.Entities.DayPrayerTimes;
import com.app.dz.quranapp.MainFragmentsParte.TimeParte.PrayerTimes;
import com.app.dz.quranapp.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class PrayerJobIntentService extends JobService {



    public void prepareAndScheduleNextPrayerTime(Context context) {
        PrayerTimesHelper prayerTimesHelper = new PrayerTimesHelper(new PrayerTimesHelper.TimesListener() {
            @Override
            public void onPrayerTimesResult(Map<String, DayPrayerTimes> TimesMap) {
                DayPrayerTimes todayPrayerTimes= TimesMap.get("today");
                DayPrayerTimes tomorrowPrayerTimes= TimesMap.get("nextDay");
                DayPrayerTimes yesterdayPrayerTimes= TimesMap.get("prevDay");

                PrayerTimes prayerTimesToday = getConvertTimeMilliSeconds(todayPrayerTimes);

                long tomorrowFajr = getTomorrowFajr(tomorrowPrayerTimes,todayPrayerTimes);
                Pair<String,Long> pair = getTheNextPrayer(prayerTimesToday,tomorrowFajr);

                //todo real one long nextPrayerTimeDelayMillis = pair.second;
                long nextPrayerTimeDelayMillis = System.currentTimeMillis() + 5 * 60 * 1000;

                updateForegroundServiceNotification(pair.first,nextPrayerTimeDelayMillis);
                schedulePrayerJob(nextPrayerTimeDelayMillis,context);
            }

            @Override
            public void onError(String error) {

            }
        }, context);
        prayerTimesHelper.getDayPrayerTimes();
    }


    private void updateForegroundServiceNotification(String timeName,long nextPrayerTime) {
        // Create an intent to start the PrayerForegroundService
        Intent serviceIntent = new Intent(this, PrayerForegroundService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, serviceIntent, PendingIntent.FLAG_IMMUTABLE);

        // Create a notification for the foreground service with updated content
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_location_icon)
                .setContentTitle("Prayer App")
                .setContentText(timeName+" " + convertMillisToTime(nextPrayerTime))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Notification updatedNotification = builder.build();

        // Use startForeground to update the existing notification in the foreground service
        startForeground(NOTIFICATION_ID, updatedNotification);

    }

    public static void schedulePrayerJob(long nextPrayerTimeMillis, Context context) {
        long currentTimeMillis = System.currentTimeMillis();
        long delayMillis = nextPrayerTimeMillis - currentTimeMillis;

        if (delayMillis > 0) {
            ComponentName serviceComponent = new ComponentName(context, PrayerJobIntentService.class);
            JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, serviceComponent)
                    .setPersisted(true)
                    .setMinimumLatency(delayMillis) // Set the delay until the job is scheduled
                    .setOverrideDeadline(delayMillis + 1000); // Set the maximum delay for the job

            JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            jobScheduler.schedule(builder.build());
            Log.e("testLog", "schedulePrayerJob done at " + convertMillisToTime(nextPrayerTimeMillis));
        } else {
            // The next prayer time is in the past, handle this case accordingly
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("testLog", "Job service destroyed ");
    }

    public static String convertMillisToTime(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date(millis));
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        // Perform background work here, e.g., send notification for prayer time

        // After handling the current prayer, schedule the next one
        long nowPrayerTimeDelayMillis = System.currentTimeMillis();

        // Show a notification when the prayer time is reached
        NotificationUtils.showPrayerNotification(this,convertMillisToTime(nowPrayerTimeDelayMillis));

        // Start the foreground service
        Log.e("testLog", " startForegroundServiceIfNotExists ");
        startForegroundServiceIfNotExists();

        prepareAndScheduleNextPrayerTime(this);

        Log.e("testLog", "onStartJob");
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.e("testLog", "onStopJob");
        return false;
    }

    private void startForegroundServiceIfNotExists() {
        if (!isNotificationActive(NOTIFICATION_ID)) {
            // Notification is not active, start the foreground service
            Log.e("testLog", "Starting foreground service");

            Intent serviceIntent = new Intent(this, PrayerForegroundService.class);
            startService(serviceIntent);

            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(new Intent(this, PrayerForegroundService.class));
            } else {
                startService(new Intent(this, PrayerForegroundService.class));
            }*/
        } else {
            Log.e("testLog", "the notification already exists ");
        }
    }

    private boolean isNotificationActive(int notificationId) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            // Get the list of active notifications and check if the specified ID exists
            StatusBarNotification[] activeNotifications = new StatusBarNotification[0];
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                activeNotifications = notificationManager.getActiveNotifications();
            }
            for (StatusBarNotification notification : activeNotifications) {
                if (notification.getId() == notificationId) {
                    return true; // Notification with the specified ID is active
                }
            }
        }
        return false; // Notification with the specified ID is not active
    }


    public static long getMidnightTimeInMillisToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        // Set the time to midnight
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return  calendar.getTimeInMillis();
    }
    public static long getMidnightTimeInMillisNextDAY() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        // Set the time to midnight
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTimeInMillis();
    }

    public static Pair<String,Long> getTheNextPrayer(PrayerTimes PrayerTimesToday, long FajrTimeTommorow) {
        long Nextmillseconds;
        Calendar currant = Calendar.getInstance();
        long MidnightTimeInMillis_NextDAY = getMidnightTimeInMillisNextDAY();
        long MidnightTimeInMillis_Today = getMidnightTimeInMillisToday();
        long currantMilliseconds = currant.getTimeInMillis();

        //test
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(MidnightTimeInMillis_NextDAY);
        Log.e("testLog", "middle time today " + c.getTime());
        c.add(Calendar.DAY_OF_MONTH, 1);
        Log.e("testLog", "middle time next day " + c.getTime());


        String nextSalatName;
        if (currantMilliseconds > PrayerTimesToday.ishaa && currantMilliseconds < MidnightTimeInMillis_NextDAY) {
            Nextmillseconds = FajrTimeTommorow;
            nextSalatName = "الفجر";
        } else if (currantMilliseconds > MidnightTimeInMillis_Today && currantMilliseconds < PrayerTimesToday.fajr) {
            Nextmillseconds = PrayerTimesToday.fajr;
            nextSalatName = "الفجر";
        } else if (currantMilliseconds > PrayerTimesToday.fajr && currantMilliseconds < PrayerTimesToday.sunrise) {
            Nextmillseconds = PrayerTimesToday.sunrise;
            nextSalatName = "الشروق";
        } else if (currantMilliseconds > PrayerTimesToday.sunrise && currantMilliseconds < PrayerTimesToday.duhr) {
            Nextmillseconds = PrayerTimesToday.duhr;
            nextSalatName = "الظهر";
        } else if (currantMilliseconds > PrayerTimesToday.duhr && currantMilliseconds < PrayerTimesToday.assr) {
            Nextmillseconds = PrayerTimesToday.assr;
            nextSalatName = "العصر";
        } else if (currantMilliseconds > PrayerTimesToday.assr && currantMilliseconds < PrayerTimesToday.maghrib) {
            Nextmillseconds = PrayerTimesToday.maghrib;
            nextSalatName = "المغرب";
        } else {
            Nextmillseconds = PrayerTimesToday.ishaa;
            nextSalatName = "العشاء";
        }

        String notifyTitle = "صلاة : " + nextSalatName ;

        long millisDelay = Nextmillseconds - System.currentTimeMillis();

        return new Pair<>(notifyTitle,Nextmillseconds);
    }

    private static long getTomorrowFajr(DayPrayerTimes nextDayPrayerTimes, DayPrayerTimes todayDayPrayerTimes) {
        DecimalFormat decimalFormat = new DecimalFormat("00");
        //tommorow fajr
        Calendar c = Calendar.getInstance();
        if (nextDayPrayerTimes == null) {
            c.add(Calendar.DAY_OF_MONTH, 1);
            c.set(Calendar.HOUR_OF_DAY, getIntHour(todayDayPrayerTimes.getFajr()));
            c.set(Calendar.MINUTE, getIntMinute(todayDayPrayerTimes.getFajr()));

        } else {
            c.set(Calendar.MONTH, nextDayPrayerTimes.getMonth() - 1);
            c.set(Calendar.DAY_OF_MONTH, nextDayPrayerTimes.getDay());
            c.set(Calendar.HOUR_OF_DAY, getIntHour(nextDayPrayerTimes.getFajr()));
            c.set(Calendar.MINUTE, getIntMinute(nextDayPrayerTimes.getFajr()));
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String dateString = dateFormat.format(c.getTime());
            Log.e("alarm", "next day " + dateString + "   " + nextDayPrayerTimes.toString());
        }
        return c.getTimeInMillis();
    }

    public static Integer getIntHour(String time) {
        return Integer.parseInt(time.substring(0, 2));
    }

    public static Integer getIntMinute(String time) {
        return Integer.parseInt(time.substring(3, 5));
    }

    public static PrayerTimes getConvertTimeMilliSeconds(DayPrayerTimes DayPrayerTimes) {
        Log.e("alarm", " ConvertTimeMilliSeconds ");

        PrayerTimes prayerTimes = new PrayerTimes();
        Calendar calendar = Calendar.getInstance();

        //fajr
        calendar.set(Calendar.HOUR_OF_DAY, getIntHour(DayPrayerTimes.getFajr()));
        calendar.set(Calendar.MINUTE, getIntMinute(DayPrayerTimes.getFajr()));
        prayerTimes.fajr = calendar.getTimeInMillis();

        //sunrise
        calendar.set(Calendar.HOUR_OF_DAY, getIntHour(DayPrayerTimes.getSunrise()));
        calendar.set(Calendar.MINUTE, getIntMinute(DayPrayerTimes.getSunrise()));
        prayerTimes.sunrise = calendar.getTimeInMillis();

        //thuhr
        calendar.set(Calendar.HOUR_OF_DAY, getIntHour(DayPrayerTimes.getDhuhr()));
        calendar.set(Calendar.MINUTE, getIntMinute(DayPrayerTimes.getDhuhr()));
        prayerTimes.duhr = calendar.getTimeInMillis();

        //assr
        calendar.set(Calendar.HOUR_OF_DAY, getIntHour(DayPrayerTimes.getAsr()));
        calendar.set(Calendar.MINUTE, getIntMinute(DayPrayerTimes.getAsr()));
        prayerTimes.assr = calendar.getTimeInMillis();

        //maghrib
        calendar.set(Calendar.HOUR_OF_DAY, getIntHour(DayPrayerTimes.getMaghrib()));
        calendar.set(Calendar.MINUTE, getIntMinute(DayPrayerTimes.getMaghrib()));
        prayerTimes.maghrib = calendar.getTimeInMillis();

        //ishaa
        calendar.set(Calendar.HOUR_OF_DAY, getIntHour(DayPrayerTimes.getIsha()));
        calendar.set(Calendar.MINUTE, getIntMinute(DayPrayerTimes.getIsha()));
        prayerTimes.ishaa = calendar.getTimeInMillis();

        return prayerTimes;
    }


}
