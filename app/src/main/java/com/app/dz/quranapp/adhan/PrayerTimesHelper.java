package com.app.dz.quranapp.adhan;

import static com.app.dz.quranapp.Api.RetrofitClient.BASE_URL;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.EmptyResultSetException;

import com.app.dz.quranapp.Api.Api;
import com.app.dz.quranapp.Api.RetrofitClient;
import com.app.dz.quranapp.Entities.DayPrayerTimes;
import com.app.dz.quranapp.MainFragmentsParte.AdkarParte.AdkarModel;
import com.app.dz.quranapp.MainFragmentsParte.TimeParte.FragmentPrayer;
import com.app.dz.quranapp.room.AppDatabase;
import com.app.dz.quranapp.room.Daos.AdkarDao;
import com.app.dz.quranapp.room.Daos.DayPrayerTimesDao;
import com.app.dz.quranapp.room.DatabaseClient;
import com.app.dz.quranapp.room.MushafDatabase;

import java.text.DecimalFormat;
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
                }, e -> {
                    if (e instanceof EmptyResultSetException) listener.onError("error in prev day EmptyResultSetException");
                    else listener.onError("error in prev day "+e.getMessage());
                }));
    }

    @NonNull
    private static String getStringTodayDate() {
        Calendar calendar = Calendar.getInstance();
        DecimalFormat decimalFormat = new DecimalFormat("00");

        String month = decimalFormat.format(calendar.get(Calendar.MONTH) + 1);
        String day = decimalFormat.format(calendar.get(Calendar.DAY_OF_MONTH));
        String date = day + "-" + month + "-" + calendar.get(Calendar.YEAR);
        return date;
    }
    private static String getStringNextDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        DecimalFormat decimalFormat = new DecimalFormat("00");

        String month = decimalFormat.format(calendar.get(Calendar.MONTH) + 1);
        String day = decimalFormat.format(calendar.get(Calendar.DAY_OF_MONTH));
        String date = day + "-" + month + "-" + calendar.get(Calendar.YEAR);
        return date;
    }

    @NonNull
    private static String getStringPresDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        DecimalFormat decimalFormat = new DecimalFormat("00");

        String month = decimalFormat.format(calendar.get(Calendar.MONTH) + 1);
        String day = decimalFormat.format(calendar.get(Calendar.DAY_OF_MONTH));
        String date = day + "-" + month + "-" + calendar.get(Calendar.YEAR);
        return date;
    }

    public void clearDesposite() {
        compositeDisposable.clear();
    }

    interface TimesListener{
         void onPrayerTimesResult(Map<String,DayPrayerTimes> TimesMap);
         void onError(String error);
    }
}
