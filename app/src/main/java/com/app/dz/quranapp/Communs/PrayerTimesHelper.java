package com.app.dz.quranapp.Communs;

import static com.app.dz.quranapp.Services.adhan.PrayerNotificationWorker.convertMillisToTime;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.app.dz.quranapp.data.room.Entities.DayPrayerTimes;
import com.app.dz.quranapp.ui.activities.MainActivityPartes.TimeParte.PrayerTimes;
import com.app.dz.quranapp.Services.adhan.PrayerForegroundService;
import com.app.dz.quranapp.Services.adhan.PrayerNotificationWorker;
import com.app.dz.quranapp.ui.models.adhan.DayPrayersConfig;
import com.app.dz.quranapp.ui.models.adhan.PrayerConfig;
import com.app.dz.quranapp.data.room.AppDatabase;
import com.app.dz.quranapp.data.room.Daos.DayPrayerTimesDao;
import com.app.dz.quranapp.data.room.DatabaseClient;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class PrayerTimesHelper {

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;
    private static final String SHARED_PREF_NAME = "my_shared_pref";
    private TimesListener listener;
    private final DayPrayerTimesDao dao;
    private final Map<String, DayPrayerTimes> resultsMap = new HashMap<>();
    private final CompositeDisposable compositeDisposable;
    private static PrayerTimesHelper mInstance;


    public static synchronized PrayerTimesHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new PrayerTimesHelper(context);
        }
        return mInstance;
    }

    public PrayerTimesHelper(Context context) {
        AppDatabase db = DatabaseClient.getInstance(context).getAppDatabase();
        dao = db.getDayPrayerTimesDao();
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        compositeDisposable = new CompositeDisposable();
    }

    public void setListener(TimesListener timesListener){
        this.listener = timesListener;
    }

    public void getDayPrayerTimes() {
        compositeDisposable.add(dao.getByDate(getStringTodayDate())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(dayPrayerTimes1 -> {
                    resultsMap.put("today", dayPrayerTimes1);
                    setNextDayPrayerTimes();
                }, e -> {
                    listener.onError("error in prev day " + e.getMessage());
                }));
    }


    private void setNextDayPrayerTimes() {
        compositeDisposable.add(dao.getByDate(getStringNextDay())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(dayPrayerTimes1 -> {
                    resultsMap.put("nextDay", dayPrayerTimes1);
                    setPreviousDayPrayerTimes();
                }, e -> listener.onError("error in prev day " + e.getMessage())));
    }

    @SuppressLint("CheckResult")
    private void setPreviousDayPrayerTimes() {
        compositeDisposable.add(dao.getByDate(getStringPresDay())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(dayPrayerTimes1 -> {
                    resultsMap.put("prevDay", dayPrayerTimes1);
                    listener.onPrayerTimesResult(resultsMap);
                    getNextPrayerTimeAndName();
                }, e -> listener.onError("error in prev day " + e.getMessage())));
    }

    private void getNextPrayerTimeAndName() {
        DayPrayerTimes todayPrayerTimes = resultsMap.get("today");
        DayPrayerTimes tomorrowPrayerTimes = resultsMap.get("nextDay");
        DayPrayerTimes yesterdayPrayerTimes = resultsMap.get("prevDay");

        PrayerTimes prayerTimesToday = getConvertTimeMilliSeconds(todayPrayerTimes);

        long tomorrowFajr = getTomorrowFajr(tomorrowPrayerTimes, todayPrayerTimes);
        PrayerInfo prayerInfo = getTheNextPrayer(prayerTimesToday, tomorrowFajr);
        listener.onNextPrayerNameAndTimeResult(prayerInfo);
    }

    @NonNull
    private static String getStringTodayDate() {
        Calendar calendar = Calendar.getInstance();
        DecimalFormat decimalFormat = new DecimalFormat("00");

        String month = decimalFormat.format(calendar.get(Calendar.MONTH) + 1);
        String day = decimalFormat.format(calendar.get(Calendar.DAY_OF_MONTH));
        return day + "-" + month + "-" + calendar.get(Calendar.YEAR);
    }

    private static String getStringNextDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        DecimalFormat decimalFormat = new DecimalFormat("00");

        String month = decimalFormat.format(calendar.get(Calendar.MONTH) + 1);
        String day = decimalFormat.format(calendar.get(Calendar.DAY_OF_MONTH));
        return day + "-" + month + "-" + calendar.get(Calendar.YEAR);
    }

    @NonNull
    private static String getStringPresDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        DecimalFormat decimalFormat = new DecimalFormat("00");

        String month = decimalFormat.format(calendar.get(Calendar.MONTH) + 1);
        String day = decimalFormat.format(calendar.get(Calendar.DAY_OF_MONTH));
        return day + "-" + month + "-" + calendar.get(Calendar.YEAR);
    }


    public static long getMidnightTimeInMillisToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        // Set the time to midnight
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
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

    public static PrayerInfo getTheNextPrayer(PrayerTimes PrayerTimesToday, long FajrTimeTommorow) {
        long Nextmillseconds;
        Calendar currant = Calendar.getInstance();
        long MidnightTimeInMillis_NextDAY = getMidnightTimeInMillisNextDAY();
        long MidnightTimeInMillis_Today = getMidnightTimeInMillisToday();
        long currantMilliseconds = currant.getTimeInMillis();

        //test
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(MidnightTimeInMillis_NextDAY);
        // Log.e("testLog", "middle time today " + c.getTime());
        c.add(Calendar.DAY_OF_MONTH, 1);
        // Log.e("testLog", "middle time next day " + c.getTime());


        String nextSalatName;
        String nextEnglsihSalatName;
        if (currantMilliseconds > PrayerTimesToday.ishaa && currantMilliseconds < MidnightTimeInMillis_NextDAY) {
            Nextmillseconds = FajrTimeTommorow;
            nextSalatName = "الفجر";
            nextEnglsihSalatName = PrayerNames.FAJR.name();
        } else if (currantMilliseconds > MidnightTimeInMillis_Today && currantMilliseconds < PrayerTimesToday.fajr) {
            Nextmillseconds = PrayerTimesToday.fajr;
            nextSalatName = "الفجر";
            nextEnglsihSalatName = PrayerNames.FAJR.name();
        } else if (currantMilliseconds > PrayerTimesToday.fajr && currantMilliseconds < PrayerTimesToday.sunrise) {
            Nextmillseconds = PrayerTimesToday.sunrise;
            nextSalatName = "الشروق";
            nextEnglsihSalatName = PrayerNames.SHOUROK.name();
        } else if (currantMilliseconds > PrayerTimesToday.sunrise && currantMilliseconds < PrayerTimesToday.duhr) {
            Nextmillseconds = PrayerTimesToday.duhr;
            nextSalatName = "الظهر";
            nextEnglsihSalatName = PrayerNames.DUHR.name();
        } else if (currantMilliseconds > PrayerTimesToday.duhr && currantMilliseconds < PrayerTimesToday.assr) {
            Nextmillseconds = PrayerTimesToday.assr;
            nextSalatName = "العصر";
            nextEnglsihSalatName = PrayerNames.ASR.name();
        } else if (currantMilliseconds > PrayerTimesToday.assr && currantMilliseconds < PrayerTimesToday.maghrib) {
            Nextmillseconds = PrayerTimesToday.maghrib;
            nextSalatName = "المغرب";
            nextEnglsihSalatName = PrayerNames.MAGHRIB.name();
        } else {
            Nextmillseconds = PrayerTimesToday.ishaa;
            nextSalatName = "العشاء";
            nextEnglsihSalatName = PrayerNames.ISHA.name();
        }
        return new PrayerInfo(nextSalatName, nextEnglsihSalatName,Nextmillseconds);
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


    public static void scheduleNextPrayerJob(Context context) {

        PrayerTimesHelper prayerTimesHelper = new PrayerTimesHelper(context);
        prayerTimesHelper.setListener(new PrayerTimesHelper.TimesListener() {
            @Override
            public void onPrayerTimesResult(Map<String,DayPrayerTimes> TimesMap) {

            }

            @Override
            public void onNextPrayerNameAndTimeResult(PrayerInfo prayerInfo) {
                long nextPrayerDelay = prayerInfo.prayer_time - System.currentTimeMillis();

                Data inputData = new Data.Builder()
                        .putString("prayerInfo",new Gson().toJson(prayerInfo,PrayerInfo.class))
                        .build();

                OneTimeWorkRequest nextPrayerWork = new OneTimeWorkRequest.Builder(PrayerNotificationWorker.class)
                        .setInputData(inputData)
                        .setInitialDelay(nextPrayerDelay,TimeUnit.MILLISECONDS).build();


                Log.e("testLog", "---->reschedule next one on " + convertMillisToTime(System.currentTimeMillis() + nextPrayerDelay));
                WorkManager.getInstance(context).enqueue(nextPrayerWork);

                //tell the shared preference that there is a scheduled work
                UUID workId = nextPrayerWork.getId();
                PrayerTimesHelper.getInstance(context).setIsThereOnSchedule(true,context);
                PrayerTimesHelper.getInstance(context).setNextScheduledWorkId(workId,context);


                //update the fixed notification title and time for next prayer
                String fixedTitle = prayerInfo.prayer_arabic + " " + convertMillisToTime(prayerInfo.prayer_time);
                startForegroundService(context, fixedTitle);
            }

            @Override
            public void onError(String error) {

            }
        });
        prayerTimesHelper.getDayPrayerTimes();

    }

    private static void startForegroundService(Context context, String title) {
        Intent serviceIntent = new Intent(context, PrayerForegroundService.class);
        serviceIntent.putExtra("title", title);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.e("testLog", "----> start foreground service " + Build.VERSION.SDK_INT);
            context.startForegroundService(serviceIntent);
        } else {
            Log.e("testLog", "----> start foreground service" + Build.VERSION.SDK_INT);
            context.startService(serviceIntent);
        }

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

    public void setNextScheduledWorkId(UUID newWorkId, Context context) {
        DayPrayersConfig dayPrayersConfig = getDayPrayersConfig(context);
        dayPrayersConfig.newWorkId = newWorkId;
        saveDayPrayersConfig(dayPrayersConfig, context);
    }

    public static DayPrayersConfig getDayPrayersConfig(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String dayString = sharedPreferences.getString("dayPrayersConfig","no");
        if (dayString.equals("no")) {
            return getDefaultPrayersConfig();
        } else {
            return new Gson().fromJson(dayString,DayPrayersConfig.class);
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
        return new DayPrayersConfig(pCFajr,pCShourok,pCDuhr,pCAsr,pCMaghrib,pCIchaa,false);
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
                    if (state == WorkInfo.State.CANCELLED) {
                        Log.e("quran_tag", "Work is cancelled");
                        PrayerTimesHelper.getInstance(context).setIsThereOnSchedule(false,context);
                        PrayerTimesHelper.scheduleNextPrayerJob(context);
                    } else {
                        Log.e("quran_tag", "Work is not cancelled");
                    }
                    Log.e("quran_tag", "Work state: " + state.name());
                } else {
                    Log.e("quran_tag", "WorkInfo is null");
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, Executors.newSingleThreadExecutor());

    }


    public void clearDesposite() {
        compositeDisposable.clear();
    }

    interface TimesListener {
        void onPrayerTimesResult(Map<String, DayPrayerTimes> TimesMap);

        void onNextPrayerNameAndTimeResult(PrayerInfo prayerInfo);

        void onError(String error);
    }

    public static class PrayerInfo {
        public String prayer_arabic;
        public String prayer_english_name;
        long prayer_time;

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
        FAJR,SHOUROK,DUHR,ASR,MAGHRIB,ISHA;
    }
    public enum AdhanSound {
        VIBRATION,SILENT,NORMAL
    }
}
