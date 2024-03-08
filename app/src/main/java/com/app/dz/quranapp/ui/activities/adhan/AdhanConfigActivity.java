package com.app.dz.quranapp.ui.activities.adhan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.app.dz.quranapp.Communs.PrayerTimesHelper;
import com.app.dz.quranapp.Communs.PrayerTimesPreference;
import com.app.dz.quranapp.Services.adhan.AdanAudioPlayerService;
import com.app.dz.quranapp.Services.adhan.PrayerNotificationWorker;
import com.app.dz.quranapp.databinding.AdhanActivityBinding;
import com.app.dz.quranapp.ui.models.adhan.DayPrayersConfig;
import com.app.dz.quranapp.ui.models.adhan.PrayerConfig;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AdhanConfigActivity extends AppCompatActivity {
    private AdhanActivityBinding binding;
    private boolean isThereChanges = false;
    private int changesPosition = -1;
    private int changeCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AdhanActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.imgClose.setOnClickListener(view -> {
            if (isThereChanges) {
                setResult(Activity.RESULT_OK);
            } else {
                setResult(Activity.RESULT_CANCELED);
            }
            finish();
        });

        /*
        binding.included.imgFilter.setVisibility(View.GONE);
        binding.included.imgFull.setVisibility(View.GONE);
        binding.included.tvTitle.setText("اعدادات الاذان");

        binding.tvTitle.setOnClickListener(view -> {
            scheduleTestPrayerWorker();
        });*/


        /*
        binding.included.tvTitle.setOnLongClickListener(view -> {
            //NotificationUtils.showPrayerNotification(AdhanConfigActivity.this,convertMillisToTime(System.currentTimeMillis()),"حان الان موعد صلاة العشاء");
            playAudioFromUrl(Constants.Adhan_Audio);
//            schedulePrayerJob();
            return true;
        });*/


        DisplayConfiguration();
    }

    private void DisplayConfiguration() {
        List<PrayerConfig> prayerConfigList = new ArrayList<>();
        // Add some dummy data for demonstration

        DayPrayersConfig dayPrayersConfig = PrayerTimesPreference.getDayPrayersConfig(this);

        Log.e("logtag", "fajr " + dayPrayersConfig.FajrConfig.toString());
        Log.e("logtag", "duhr " + dayPrayersConfig.DuhrConfig.toString());
        Log.e("logtag", "asr " + dayPrayersConfig.AsrConfig.toString());
        Log.e("logtag", "maghrib " + dayPrayersConfig.MaghribConfig.toString());
        Log.e("logtag", "Ichaa " + dayPrayersConfig.IchaaConfig.toString());

        prayerConfigList.add(dayPrayersConfig.FajrConfig);
        prayerConfigList.add(dayPrayersConfig.ShourokConfig);
        prayerConfigList.add(dayPrayersConfig.DuhrConfig);
        prayerConfigList.add(dayPrayersConfig.AsrConfig);
        prayerConfigList.add(dayPrayersConfig.MaghribConfig);
        prayerConfigList.add(dayPrayersConfig.IchaaConfig);

        // Set up RecyclerView
        PrayerConfigAdapter adapter = new PrayerConfigAdapter(prayerConfigList, position -> {
            isThereChanges = true;
            changesPosition = position;
            changeCount++;
        });
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
    }

    public void playAudioFromUrl(String url) {
        Intent intent = new Intent(this, AdanAudioPlayerService.class);
        intent.setAction(AdanAudioPlayerService.ACTION_PLAY);
        intent.putExtra("url", url);
        startService(intent);
    }


    private void scheduleTestPrayerWorker() {
        PrayerTimesPreference.PrayerInfo prayerInfo = new PrayerTimesPreference.PrayerInfo("", PrayerTimesPreference.PrayerNames.ASR.name(), 445645);
        Data inputData = new Data.Builder()
                .putString("prayerInfo", new Gson().toJson(prayerInfo, PrayerTimesPreference.PrayerInfo.class))
                .build();


        OneTimeWorkRequest nextPrayerWork = new OneTimeWorkRequest.Builder(PrayerNotificationWorker.class)
                .setInputData(inputData)
                .setInitialDelay(5, TimeUnit.MINUTES).build();


        Log.e("testLog", "---->reschedule next one on 5 min ");
        WorkManager.getInstance(AdhanConfigActivity.this).enqueue(nextPrayerWork);
    }

    @Override
    public void onBackPressed() {
        if (isThereChanges) {
            setResult(Activity.RESULT_OK);
        } else {
            setResult(Activity.RESULT_CANCELED);
        }
        super.onBackPressed();
    }

}
