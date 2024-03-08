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

    public PrayerNotificationWorker(@NonNull Context contextt,@NonNull WorkerParameters workerParams) {
        super(contextt, workerParams);
        context = contextt;
    }


    @NonNull
    @Override
    public Result doWork() {

        String prayerInfoStrinf = getInputData().getString("prayerInfo");
        PrayerTimesPreference.PrayerInfo prayerInfo = new Gson().fromJson(prayerInfoStrinf, PrayerTimesPreference.PrayerInfo.class);
        prepareNextPrayerTime(prayerInfo,context);
        // Indicate that the work was successful
        return Result.success();
    }

    public static void prepareNextPrayerTime(PrayerTimesPreference.PrayerInfo prayerInfo,Context context) {
        showAndUpdateNotification(prayerInfo,context);
        PrayerTimesHelper.scheduleNextPrayerJob(context);
    }
        public static void showAndUpdateNotification(PrayerTimesPreference.PrayerInfo prayerInfo,Context context) {


        //show the current prayer notification
        NotificationUtils.showPrayerNotification(context,convertMillisToTime(System.currentTimeMillis()), prayerInfo.prayer_arabic);

        //play the adhan sound
        PrayerConfig prayerConfig = PrayerTimesPreference.getInstance(context).getPrayerConfigWithName(context,prayerInfo.prayer_english_name);
        if (prayerConfig == null) {
            Log.e("testLog", "prayerConfig is null so we dont play adhan audio");
            return;
        }
        if (prayerConfig.soundType.equals(PrayerTimesPreference.AdhanSound.NORMAL.name()) && prayerConfig.isNotifyOnSilentMode) {
            Log.e("testLog","prayerConfig conditions are granted so play adhan audio");
            playAudioFromUrl(Constants.Adhan_Audio,context);
        } else {
            Log.e("testLog","prayerConfig conditions are not granted so we dont play adhan audio isNotifyOnSilentMode : "
                    +prayerConfig.isNotifyOnSilentMode+" AdhanSoundValue "+prayerConfig.soundType);
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

    public static void playAudioFromUrl(String url,Context context) {
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
