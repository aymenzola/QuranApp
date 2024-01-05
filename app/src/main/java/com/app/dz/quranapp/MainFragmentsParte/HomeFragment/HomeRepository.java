package com.app.dz.quranapp.MainFragmentsParte.HomeFragment;

import static com.app.dz.quranapp.Api.RetrofitClient.BASE_URL;

import android.annotation.SuppressLint;
import android.app.Application;
import android.util.Log;

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
import java.util.List;
import java.util.Random;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class HomeRepository {

    private final DayPrayerTimesDao dao;
    private final AdkarDao daoDikr;
    private final MutableLiveData<AdkarModel> adkarModelMutableLiveData;
    private final MutableLiveData<DayPrayerTimes> dayPrayerTimes;
    private final MutableLiveData<DayPrayerTimes> nextDayPrayerTimes;
    private final MutableLiveData<DayPrayerTimes> previousDayPrayerTimes;
    private final MutableLiveData<List<DayPrayerTimes>> WeekdayPrayerTimesList;
    private final CompositeDisposable compositeDisposable;
    private final Api api;

    public HomeRepository(Application application) {
        MushafDatabase database = MushafDatabase.getInstance(application);
        AppDatabase db = DatabaseClient.getInstance(application).getAppDatabase();
        api = RetrofitClient.getInstance(BASE_URL).getApi();
        dao = db.getDayPrayerTimesDao();
        daoDikr = database.getAdkarDao();
        adkarModelMutableLiveData = new MutableLiveData<>();
        dayPrayerTimes = new MutableLiveData<>();
        nextDayPrayerTimes = new MutableLiveData<>();
        WeekdayPrayerTimesList = new MutableLiveData<>();
        previousDayPrayerTimes = new MutableLiveData<>();
        compositeDisposable = new CompositeDisposable();
    }

    public LiveData<AdkarModel> getRandomDikr() {
        return adkarModelMutableLiveData;
    }
    public LiveData<DayPrayerTimes> getDayPrayerTimes() {
        return dayPrayerTimes;
    }

    public LiveData<DayPrayerTimes> getNextDayPrayerTimes() {
        return nextDayPrayerTimes;
    }

    public LiveData<DayPrayerTimes> getPreviousDayPrayerTimes() {
        return previousDayPrayerTimes;
    }

    public LiveData<List<DayPrayerTimes>> getWeekPrayerTimes() {
        return WeekdayPrayerTimesList;
    }

    public void setDayPrayerTimes() {

        Calendar calendar = Calendar.getInstance();
        DecimalFormat decimalFormat = new DecimalFormat("00");

        String month = decimalFormat.format(calendar.get(Calendar.MONTH) + 1);
        String day = decimalFormat.format(calendar.get(Calendar.DAY_OF_MONTH));
        String date = day + "-" + month + "-" + calendar.get(Calendar.YEAR);

        Log.e("checkdata", "we are getting data " + date);
        compositeDisposable.add(dao.getByDate(date)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(dayPrayerTimes1 -> {
                    Log.e("checkdata", "1 data coming  " + dayPrayerTimes1);
                    dayPrayerTimes.setValue(dayPrayerTimes1);
                }, e -> {
                    if (e instanceof EmptyResultSetException) dayPrayerTimes.setValue(null);
                    else Log.e("alarm", "1 data error   " + e.getMessage());
                }));
    }

    public void setNextDayPrayerTimes() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        DecimalFormat decimalFormat = new DecimalFormat("00");

        String month = decimalFormat.format(calendar.get(Calendar.MONTH) + 1);
        String day = decimalFormat.format(calendar.get(Calendar.DAY_OF_MONTH));
        String date = day + "-" + month + "-" + calendar.get(Calendar.YEAR);

        Log.e("alarm", "next day is " + date);

        Log.e("checkdata", "we are getting data");
        compositeDisposable.add(dao.getByDate(date)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(dayPrayerTimes1 -> {
                    Log.e("checkdata", "1 data coming  " + dayPrayerTimes1);
                    nextDayPrayerTimes.setValue(dayPrayerTimes1);
                }, e -> {
                    if (e instanceof EmptyResultSetException) nextDayPrayerTimes.setValue(null);
                    else Log.e("alarm", "1 data error   " + e.getMessage());
                }));
    }

    @SuppressLint("CheckResult")
    public void setPriveusDayPrayerTimes() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        DecimalFormat decimalFormat = new DecimalFormat("00");

        String month = decimalFormat.format(calendar.get(Calendar.MONTH) + 1);
        String day = decimalFormat.format(calendar.get(Calendar.DAY_OF_MONTH));
        String date = day + "-" + month + "-" + calendar.get(Calendar.YEAR);

        Log.e("alarm", "we call previous day is " + date);

        Log.e("checkdata", "we are getting data");
        compositeDisposable.add(dao.getByDate(date)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(dayPrayerTimes1 -> {
                    Log.e("alarm", "previous day data coming  " + dayPrayerTimes1);
                    previousDayPrayerTimes.setValue(dayPrayerTimes1);
                }, e -> {
                    if (e instanceof EmptyResultSetException) previousDayPrayerTimes.setValue(null);
                    else Log.e("alarm", "1 data error   " + e.getMessage());
                }));
    }

    @SuppressLint("CheckResult")
    public void setWeekTimes(String[] nextDates) {

        Log.e(FragmentPrayer.TAG, "we are getting data");

        compositeDisposable.add(dao.getDaysForCurrentWeek(nextDates)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(dayPrayerTimesList -> {
                    Log.e(FragmentPrayer.TAG, "week data coming  " + dayPrayerTimesList.size());
                    WeekdayPrayerTimesList.setValue(dayPrayerTimesList);
                }, e -> {
                    if (e instanceof EmptyResultSetException) WeekdayPrayerTimesList.setValue(null);
                    else Log.e(FragmentPrayer.TAG, "1 data week error   " + e.getMessage());
                }));
    }

    public void setRandomDikr() {
        Random random = new Random();
        int id = random.nextInt(37) + 2;
        compositeDisposable.add(daoDikr.getdikrWithId(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(adkarModelMutableLiveData::postValue, e->{
                    Log.e("checkdata","adkar data error   "+e.getMessage());
                }));
    }

    public void clearDesposite() {
        compositeDisposable.clear();
    }
}
