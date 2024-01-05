package com.app.dz.quranapp.adhan;

import static com.app.dz.quranapp.Api.RetrofitClient.BASE_URL;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.EmptyResultSetException;

import com.app.dz.quranapp.Api.Api;
import com.app.dz.quranapp.Api.RetrofitClient;
import com.app.dz.quranapp.Entities.DayPrayerTimes;
import com.app.dz.quranapp.MainFragmentsParte.AdkarParte.AdkarModel;
import com.app.dz.quranapp.MainFragmentsParte.TimeParte.FragmentPrayer;
import com.app.dz.quranapp.MainFragmentsParte.TimeParte.PrayerTimes;
import com.app.dz.quranapp.room.AppDatabase;
import com.app.dz.quranapp.room.Daos.AdkarDao;
import com.app.dz.quranapp.room.Daos.DayPrayerTimesDao;
import com.app.dz.quranapp.room.DatabaseClient;
import com.app.dz.quranapp.room.MushafDatabase;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class PrayerTimesHelper {

    private final TimesListener listener;
    private final DayPrayerTimesDao dao;
    private final Map<String,DayPrayerTimes> resultsMap = new HashMap<>();
    private final CompositeDisposable compositeDisposable;
    private final Api api;

    public PrayerTimesHelper(TimesListener timesListener, Context context) {
        listener = timesListener;
        AppDatabase db = DatabaseClient.getInstance(context).getAppDatabase();
        api = RetrofitClient.getInstance(BASE_URL).getApi();
        dao = db.getDayPrayerTimesDao();
        compositeDisposable = new CompositeDisposable();
    }


    public void getDayPrayerTimes() {
        compositeDisposable.add(dao.getByDate(getStringTodayDate())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(dayPrayerTimes1 -> {
                    resultsMap.put("today",dayPrayerTimes1);
                    setNextDayPrayerTimes();
                    }, e -> {
                    if (e instanceof EmptyResultSetException) listener.onError("error in prev day EmptyResultSetException");
                    else listener.onError("error in prev day "+e.getMessage());
                }));
    }



    private void setNextDayPrayerTimes() {
        compositeDisposable.add(dao.getByDate(getStringNextDay())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(dayPrayerTimes1 -> {
                    resultsMap.put("nextDay",dayPrayerTimes1);
                    setPreviousDayPrayerTimes();
                }, e -> {
                    if (e instanceof EmptyResultSetException) listener.onError("error in prev day EmptyResultSetException");
                    else listener.onError("error in prev day "+e.getMessage());
                }));
    }

    @NonNull
    @SuppressLint("CheckResult")
    private void setPreviousDayPrayerTimes() {

        compositeDisposable.add(dao.getByDate(getStringPresDay())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(dayPrayerTimes1 -> {
                    resultsMap.put("prevDay",dayPrayerTimes1);
                    listener.onPrayerTimesResult(resultsMap);
                    getNextPrayerTimeAndName();

                }, e -> {
                    if (e instanceof EmptyResultSetException) listener.onError("error in prev day EmptyResultSetException");
                    else listener.onError("error in prev day "+e.getMessage());
                }));
    }

    private  void getNextPrayerTimeAndName() {
        DayPrayerTimes todayPrayerTimes= resultsMap.get("today");
        DayPrayerTimes tomorrowPrayerTimes= resultsMap.get("nextDay");
        DayPrayerTimes yesterdayPrayerTimes= resultsMap.get("prevDay");

        PrayerTimes prayerTimesToday = getConvertTimeMilliSeconds(todayPrayerTimes);

        long tomorrowFajr = getTomorrowFajr(tomorrowPrayerTimes,todayPrayerTimes);
        Pair<String,Long> pair = getTheNextPrayer(prayerTimesToday,tomorrowFajr);
        listener.onPrayerNameAndTimeResult(pair);
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
       // Log.e("testLog", "middle time today " + c.getTime());
        c.add(Calendar.DAY_OF_MONTH, 1);
       // Log.e("testLog", "middle time next day " + c.getTime());


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

    public void clearDesposite() {
        compositeDisposable.clear();
    }

    interface TimesListener{
         void onPrayerTimesResult(Map<String,DayPrayerTimes> TimesMap);
         void onPrayerNameAndTimeResult(Pair<String,Long> NmaeTimePair);
         void onError(String error);
    }
}
