package com.app.dz.quranapp.Services.adhan;

import static com.app.dz.quranapp.Util.PublicMethods.isDeviceInSilentMode;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.ForegroundInfo;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.app.dz.quranapp.Communs.Constants;
import com.app.dz.quranapp.Communs.PrayerTimesHelper;
import com.app.dz.quranapp.Communs.PrayerTimesPreference;
import com.app.dz.quranapp.Util.NotificationUtils;
import com.app.dz.quranapp.ui.models.adhan.PrayerConfig;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PrayerNotificationWorker extends Worker {
    private final Context context;

    public PrayerNotificationWorker(@NonNull Context contextt, @NonNull WorkerParameters workerParams) {
        super(contextt, workerParams);
        context = contextt;
    }


    @NonNull
    @Override
    public Result doWork() {

        String prayerInfoStrinf = getInputData().getString("prayerInfo");
        PrayerTimesPreference.PrayerInfo prayerInfo = new Gson().fromJson(prayerInfoStrinf, PrayerTimesPreference.PrayerInfo.class);

        PrayerTimesPreference.PrayerInfo nextPrayerInfo = PrayerTimesPreference.getInstance(context).getNextScheduleNotification();

        //condition to prevent double notification on the same prayer time
        if (nextPrayerInfo.prayer_time != prayerInfo.prayer_time) {
            //this means that the next prayer time has been scheduled and the user have seen the notification of this prayer time
            return Result.success();
        }

        prepareNextPrayerTime(prayerInfo,context);
        return Result.success();
    }

    public static void prepareNextPrayerTime(PrayerTimesPreference.PrayerInfo prayerInfo, Context context) {
        showAndUpdateNotification(prayerInfo, context);
        PrayerTimesHelper.scheduleNextPrayerJob(context);
    }

    public static void showAndUpdateNotification(PrayerTimesPreference.PrayerInfo prayerInfo, Context context) {


        //show the current prayer notification

        //check if the difference between current time and the scheduled time is less than 3 minutes
        //if yes then we show the notification
        long diff = prayerInfo.prayer_time - System.currentTimeMillis();
        if (diff > 3 * 60 * 1000) {
            Log.e("testLog", "the difference between current time and the scheduled time is more than 3 minutes so we dont show the notification");
            /*if (BuildConfi.DEBUG) {

            }*/
            return;
        }


        NotificationUtils.showPrayerNotification(context, convertMillisToTime(System.currentTimeMillis()), prayerInfo.prayer_arabic);

        //play the adhan sound
        PrayerConfig prayerConfig = PrayerTimesPreference.getInstance(context).getPrayerConfigWithName(context, prayerInfo.prayer_english_name);
        if (prayerConfig == null) {
            Log.e("testLog", "prayerConfig is null so we dont play adhan audio");
            return;
        }
        if (prayerConfig.soundType.equals(PrayerTimesPreference.AdhanSound.NORMAL.name()) && prayerConfig.isNotifyOnSilentMode) {
            Log.e("testLog", "prayerConfig conditions are granted so play adhan audio");
            playAudioFromUrl(Constants.Adhan_Audio, context);
        } else {
            Log.e("testLog", "prayerConfig conditions are not granted so we dont play adhan audio isNotifyOnSilentMode : "
                    + prayerConfig.isNotifyOnSilentMode + " AdhanSoundValue " + prayerConfig.soundType);
        }

    }

    public static String convertMillisToTime(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date(millis));
    }


    @NonNull
    @Override
    public ListenableFuture<ForegroundInfo> getForegroundInfoAsync() {
        return super.getForegroundInfoAsync();
    }


    @Override
    public void onStopped() {
        super.onStopped();
    }

    public static void playAudioFromUrl(String url, Context context) {
        if (isDeviceInSilentMode(context)) {
            Log.e("quran_tag", "Device is in silent mode");
            return;
        }
        Intent intent = new Intent(context, AdanAudioPlayerService.class);
        intent.setAction(AdanAudioPlayerService.ACTION_PLAY);
        intent.putExtra("url", url);
        context.startService(intent);
    }


}
