package com.app.dz.quranapp.Communs;

import static com.app.dz.quranapp.ui.activities.adhan.BootReceiver.TAG_BROAD_CAST;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.app.dz.quranapp.Services.adhan.AlarmBroadcastReceiver;
import com.app.dz.quranapp.data.room.Entities.DayPrayerTimes;
import com.app.dz.quranapp.ui.models.adhan.DayPrayersConfig;
import com.app.dz.quranapp.ui.models.adhan.PrayerConfig;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import io.reactivex.disposables.CompositeDisposable;

public class PrayerTimesPreference {

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;
    private static final String SHARED_PREF_NAME = "my_shared_pref";
    private final CompositeDisposable compositeDisposable;
    private static PrayerTimesPreference mInstance;


    public static synchronized PrayerTimesPreference getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new PrayerTimesPreference(context);
        }
        return mInstance;
    }

    public PrayerTimesPreference(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        compositeDisposable = new CompositeDisposable();
    }



    public void saveDayPrayersConfig(DayPrayersConfig dayPrayersConfig, Context context) {
        editor.putString("dayPrayersConfig", new Gson().toJson(dayPrayersConfig, DayPrayersConfig.class));
        editor.apply();
    }

    public void setIsThereOnSchedule(boolean isThereOnSchedule, Context context) {
        DayPrayersConfig dayPrayersConfig = getDayPrayersConfig(context);
        dayPrayersConfig.isThereOnSchedule = isThereOnSchedule;
        saveDayPrayersConfig(dayPrayersConfig, context);
    }

    public void saveNextScheduleNotification(PrayerInfo prayerInfo) {
        editor.putString("nextNotifPrayersConfig", new Gson().toJson(prayerInfo,PrayerInfo.class)).apply();
    }
    public PrayerInfo getNextScheduleNotification() {
        String nextNotifPrayersConfig = sharedPreferences.getString("nextNotifPrayersConfig", "no");
        if (nextNotifPrayersConfig.equals("no")) {
            return null;
        } else {
            return new Gson().fromJson(nextNotifPrayersConfig,PrayerInfo.class);
        }
    }

    public void setNextScheduledWorkId(UUID newWorkId, Context context) {
        DayPrayersConfig dayPrayersConfig = getDayPrayersConfig(context);
        dayPrayersConfig.newWorkId = newWorkId;
        saveDayPrayersConfig(dayPrayersConfig, context);
    }

    public static DayPrayersConfig getDayPrayersConfig(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String dayString = sharedPreferences.getString("dayPrayersConfig", "no");
        if (dayString.equals("no")) {
            return getDefaultPrayersConfig();
        } else {
            return new Gson().fromJson(dayString, DayPrayersConfig.class);
        }
    }


    public PrayerConfig getPrayerConfigWithName(Context context, String name) {
        DayPrayersConfig dayPrayersConfig = getDayPrayersConfig(context);
        return switch (name) {
            case "FAJR" -> dayPrayersConfig.FajrConfig;
            case "DUHR" -> dayPrayersConfig.DuhrConfig;
            case "ASR" -> dayPrayersConfig.AsrConfig;
            case "MAGHRIB" -> dayPrayersConfig.MaghribConfig;
            case "ISHA" -> dayPrayersConfig.IchaaConfig;
            default -> null;
        };
    }

    @NonNull
    private static DayPrayersConfig getDefaultPrayersConfig() {
        PrayerConfig pCFajr = new PrayerConfig(PrayerNames.FAJR.name(), AdhanSound.NORMAL.name(), false);
        PrayerConfig pCShourok = new PrayerConfig(PrayerNames.SHOUROK.name(), AdhanSound.SILENT.name(), false);
        PrayerConfig pCDuhr = new PrayerConfig(PrayerNames.DUHR.name(), AdhanSound.NORMAL.name(), false);
        PrayerConfig pCAsr = new PrayerConfig(PrayerNames.ASR.name(), AdhanSound.NORMAL.name(), false);
        PrayerConfig pCMaghrib = new PrayerConfig(PrayerNames.MAGHRIB.name(), AdhanSound.NORMAL.name(), false);
        PrayerConfig pCIchaa = new PrayerConfig(PrayerNames.ISHA.name(), AdhanSound.NORMAL.name(), false);
        return new DayPrayersConfig(pCFajr, pCShourok, pCDuhr, pCAsr, pCMaghrib, pCIchaa, false);
    }

    public static void turnOffAllPrayersConfig(Context context) {
        PrayerConfig pCFajr = new PrayerConfig(PrayerNames.FAJR.name(), AdhanSound.SILENT.name(), false);
        PrayerConfig pCShourok = new PrayerConfig(PrayerNames.SHOUROK.name(), AdhanSound.SILENT.name(), false);
        PrayerConfig pCDuhr = new PrayerConfig(PrayerNames.DUHR.name(), AdhanSound.SILENT.name(), false);
        PrayerConfig pCAsr = new PrayerConfig(PrayerNames.ASR.name(), AdhanSound.SILENT.name(), false);
        PrayerConfig pCMaghrib = new PrayerConfig(PrayerNames.MAGHRIB.name(), AdhanSound.SILENT.name(), false);
        PrayerConfig pCIchaa = new PrayerConfig(PrayerNames.ISHA.name(), AdhanSound.SILENT.name(), false);
        DayPrayersConfig dayPrayersConfig = new DayPrayersConfig(pCFajr, pCShourok, pCDuhr, pCAsr, pCMaghrib, pCIchaa, false);
        mInstance.saveDayPrayersConfig(dayPrayersConfig,context);
    }

    public void checkIfTheWorkIsScheduler(Context context) {
        DayPrayersConfig dayPrayersConfig = getDayPrayersConfig(context);
        if (dayPrayersConfig.newWorkId == null) {
            return;
        }

        ListenableFuture<WorkInfo> future = WorkManager.getInstance(context).getWorkInfoById(dayPrayersConfig.newWorkId);
        future.addListener(() -> {
            try {
                WorkInfo workInfo = future.get();
                if (workInfo != null) {
                    WorkInfo.State state = workInfo.getState();
                    if (isFinished(state)) {
                        Log.e(TAG_BROAD_CAST, "Work is finished");
                        PrayerTimesPreference.getInstance(context).setIsThereOnSchedule(false, context);
                        PrayerTimesHelper.scheduleNextPrayerJob(context);
                    } else {
                        Log.e(TAG_BROAD_CAST, "Work is not finished");
                    }
                    Log.e(TAG_BROAD_CAST, "Work state: " + state.name());
                } else {
                    PrayerTimesPreference.getInstance(context).setIsThereOnSchedule(false, context);
                    PrayerTimesHelper.scheduleNextPrayerJob(context);
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, Executors.newSingleThreadExecutor());

    }

    public boolean isFinished(WorkInfo.State state) {
        return (state == WorkInfo.State.SUCCEEDED || state == WorkInfo.State.FAILED || state == WorkInfo.State.CANCELLED);
    }

    public void clearDesposite() {
        compositeDisposable.clear();
    }


    public static void scheduleNextAlarm(Context context,long nextPrayerDelay, PrayerInfo prayerInfo) {

        // Create an intent that points to the BroadcastReceiver that you want to trigger
        Intent intent = new Intent(context,AlarmBroadcastReceiver.class);
        intent.putExtra("prayerInfo", new Gson().toJson(prayerInfo, PrayerInfo.class));
        // Create a PendingIntent with that intent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0,intent,PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + nextPrayerDelay, pendingIntent);
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + nextPrayerDelay, pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + nextPrayerDelay, pendingIntent);
            }
        }
    }


    interface TimesListener {
        void onPrayerTimesResult(Map<String, DayPrayerTimes> TimesMap);

        void onNextPrayerNameAndTimeResult(PrayerInfo prayerInfo);

        void onError(String error);
    }

    public static class PrayerInfo {
        public String prayer_arabic;
        public String prayer_english_name;
        public long prayer_time;

        public PrayerInfo(String prayer_arabic, String prayer_english_name, long prayer_time) {
            this.prayer_arabic = prayer_arabic;
            this.prayer_english_name = prayer_english_name;
            this.prayer_time = prayer_time;
        }
    }

    public static String getPrayerArabicName(String prayerName) {
        return switch (prayerName) {
            case "FAJR" -> "الفجر";
            case "SHOUROK" -> "الشروق";
            case "DUHR" -> "الظهر";
            case "ASR" -> "العصر";
            case "MAGHRIB" -> "المغرب";
            case "ISHA" -> "العشاء";
            default -> "";
        };
    }


    public enum PrayerNames {
        FAJR, SHOUROK, DUHR, ASR, MAGHRIB, ISHA;
    }

    public enum AdhanSound {
        VIBRATION, SILENT, NORMAL
    }
}
