package com.app.dz.quranapp.ui.activities.MainActivityPartes.TimeParte;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.app.dz.quranapp.data.room.Entities.DayPrayerTimes;

import java.util.List;


public class PrayerViewModel extends AndroidViewModel {

    private final PrayerRepository repository;
    private MutableLiveData<String> result;


    public PrayerViewModel(@NonNull Application application) {
        super(application);
        result = new MutableLiveData<>();
        repository = new PrayerRepository(application);
    }

    public LiveData<DayPrayerTimes> getDayPrayer() {
        return repository.getDayPrayerTimes();
    }

    public LiveData<DayPrayerTimes> getNextDayPrayer() {
        return repository.getNextDayPrayerTimes();
    }

    public LiveData<DayPrayerTimes> getPreviousDayPrayer() {
        return repository.getPreviousDayPrayerTimes();
    }


    public LiveData<List<DayPrayerTimes>> getWeekPrayer() {
        return repository.getWeekPrayerTimes();
    }

    public void setDayPrayer() {
        repository.setDayPrayerTimes();
    }

    public void setPreviousDayPrayer() {
        repository.setPriveusDayPrayerTimes();
    }

    public void setWeekPrayer(String[] nextDates) {
        repository.setWeekTimes(nextDates);
    }

    public void setNextDayPrayer() {
        repository.setNextDayPrayerTimes();
    }


    public LiveData<String> getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result.postValue(result);
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        repository.clearDesposite();
    }

}

