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
import com.app.dz.quranapp.Util.NotificationUtils;
import com.app.dz.quranapp.Communs.PrayerTimesHelper;
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
        PrayerTimesHelper.PrayerInfo prayerInfo = new Gson().fromJson(prayerInfoStrinf, PrayerTimesHelper.PrayerInfo.class);

        prepareAndScheduleNextPrayerTime(prayerInfo);
        // Indicate that the work was successful
        return Result.success();
    }

    public void prepareAndScheduleNextPrayerTime(PrayerTimesHelper.PrayerInfo prayerInfo) {

        //scheduleNextPrayerJob and update the next prayer time in fixed notification

        //todo hide only whiletesting
        //PrayerTimesHelper.scheduleNextPrayerJob(context);

        //show the current prayer notifaication
        NotificationUtils.showPrayerNotification(context,convertMillisToTime(System.currentTimeMillis()), prayerInfo.prayer_arabic);

        //play the adhan sound
        PrayerConfig prayerConfig = PrayerTimesHelper.getInstance(context).getPrayerConfigWithName(getApplicationContext(),prayerInfo.prayer_english_name);
        if (prayerConfig == null) {
            Log.e("testLog", "prayerConfig is null so we dont play adhan audio");
            return;
        }
        if (prayerConfig.soundType.equals(PrayerTimesHelper.AdhanSound.NORMAL.name()) && prayerConfig.isNotifyOnSilentMode) {
            Log.e("testLog","prayerConfig conditions are granted so play adhan audio");
            playAudioFromUrl(Constants.Adhan_Audio);
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

    public void playAudioFromUrl(String url) {
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
