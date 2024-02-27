package com.app.dz.quranapp.ui.activities.MainActivityPartes.HomeFragment;

import static com.app.dz.quranapp.data.Api.RetrofitClient.BASE_URL;

import android.annotation.SuppressLint;
import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.EmptyResultSetException;

import com.app.dz.quranapp.data.Api.Api;
import com.app.dz.quranapp.data.Api.RetrofitClient;
import com.app.dz.quranapp.data.room.Entities.DayPrayerTimes;
import com.app.dz.quranapp.ui.activities.AdkarParte.AdkarModel;
import com.app.dz.quranapp.ui.activities.MainActivityPartes.TimeParte.FragmentPrayer;
import com.app.dz.quranapp.data.room.AppDatabase;
import com.app.dz.quranapp.data.room.Daos.AdkarDao;
import com.app.dz.quranapp.data.room.Daos.DayPrayerTimesDao;
import com.app.dz.quranapp.data.room.DatabaseClient;
import com.app.dz.quranapp.data.room.MushafDatabase;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class HomeRepository {

    private final DayPrayerTimesDao dao;
    private final AdkarDao daoDikr;
    private final MutableLiveData<AdkarModel> adkarModelMutableLiveData;
    private final MutableLiveData<List<AdkarModel>> fastAdkarListMutableLiveData;
    private final MutableLiveData<List<AdkarModel>> adkarListMutableLiveData;
    private final MutableLiveData<List<AdkarModel>> adkarListByCategoryML;
    private final MutableLiveData<List<AdkarModel>> savedAdkarListML;
    private final MutableLiveData<Boolean> dikrSaveResaultML;
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
        fastAdkarListMutableLiveData = new MutableLiveData<>();
        adkarListMutableLiveData = new MutableLiveData<>();
        adkarListByCategoryML = new MutableLiveData<>();
        dikrSaveResaultML = new MutableLiveData<>();
        savedAdkarListML = new MutableLiveData<>();
        compositeDisposable = new CompositeDisposable();
    }


    public MutableLiveData<List<AdkarModel>> getSavedAdkarListML() {
        return savedAdkarListML;
    }

    public MutableLiveData<List<AdkarModel>> getFastAdkarListMutableLiveData() {
        return fastAdkarListMutableLiveData;
    }

    public MutableLiveData<List<AdkarModel>> getAdkarListByCategoryML() {
        return adkarListByCategoryML;
    }

    public MutableLiveData<List<AdkarModel>> getAdkarListMutableLiveData() {
        return adkarListMutableLiveData;
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
                .subscribe(adkarModelMutableLiveData::postValue, e -> {
                    Log.e("checkdata", "adkar data error   " + e.getMessage());
                }));
    }


    public void setAdkarList() {
        compositeDisposable.add(daoDikr.getAdkarGroupedByCategoryId()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(adkarListMutableLiveData::postValue, e -> {
                    Log.e("checkdata", "adkar data error   " + e.getMessage());
                }));
    }


    public void setFastDikr() {
        // Create a list of AdkarModel objects
        List<AdkarModel> adkarList = new ArrayList<>();

        // Arabic adkar array
        String[] adkarArray = {"الحمد لله", "سبحان الله", "لا إله إلا الله", "الله أكبر", "لا حول ولا قوة إلا بالله",
                "أستغفر الله", "سبحان الله وبحمده", "سبحان الله العظيم", "سبحان الله وبحمده سبحان الله العظيم",
                "اللهم صل على محمد", "اللهم بارك على محمد", "لا إله إلا أنت سبحانك إني كنت من الظالمين",
                "رب اغفر لي وتب علي إنك أنت التواب الرحيم", "رب اغفر لي ولوالدي", "رب ارحمهما كما ربياني صغيرا",
                "رب اجعلني مقيم الصلاة ومن ذريتي", "ربنا واعتصمنا وانصرنا على القوم الكافرين",
                "ربنا آتنا في الدنيا حسنة وفي الآخرة حسنة وقنا عذاب النار", "ربنا لا تزغ قلوبنا بعد إذ هديتنا",
                "ربنا آمنا فاغفر لنا وارحمنا وأنت خير الراحمين"};

        // Loop to create 20 AdkarModel objects
        for (int i = 0; i < 20; i++) {
            adkarList.add(new AdkarModel(0, adkarArray[i], "Category " + (i + 1), "Source " + (i + 1), "", ""));
        }

        // Post the list to adkarModelMutableLiveData
        fastAdkarListMutableLiveData.postValue(adkarList);
    }

    public void clearDesposite() {
        compositeDisposable.clear();
    }

    public void setAdkarListByCategory(Integer categoryId) {
        Log.e("checkdata", "categoryId "+categoryId);
        compositeDisposable.add(daoDikr.getAdkarByCategoryId(categoryId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(adkarListByCategoryML::postValue, e -> {
                    Log.e("checkdata", "adkar data error   " + e.getMessage());
                }));
    }

    public MutableLiveData<Boolean> getDikrSaveResultML() {
        return dikrSaveResaultML;
    }

    public void updateDikrSaveState(Integer dikrId, Integer isSaved) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                int result = daoDikr.updateIsSaved(dikrId,isSaved);
                boolean isUpdateSuccessful = result == 1;
                // Use postValue since we're in a background thread
                dikrSaveResaultML.postValue(isUpdateSuccessful);
            } catch (Exception e) {
                Log.e("checkdata", "adkar data error   " + e.getMessage());
            }
        });
    }


    public void askForSavedAdkar() {
        compositeDisposable.add(daoDikr.getSavedAskar()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(savedAdkarListML::postValue, e -> {
                    Log.e("checkdata", "adkar data error   " + e.getMessage());
                }));
    }
}
