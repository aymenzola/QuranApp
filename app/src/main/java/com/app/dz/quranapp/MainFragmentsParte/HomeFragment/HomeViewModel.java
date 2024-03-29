package com.app.dz.quranapp.MainFragmentsParte.HomeFragment;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.app.dz.quranapp.Entities.DayPrayerTimes;
import com.app.dz.quranapp.MainFragmentsParte.AdkarParte.AdkarModel;
import com.app.dz.quranapp.MainFragmentsParte.TimeParte.PrayerRepository;

import java.util.List;


public class HomeViewModel extends AndroidViewModel {

    private final HomeRepository repository;
    private MutableLiveData<String> result;


    public HomeViewModel(@NonNull Application application) {
        super(application);
        result = new MutableLiveData<>();
        repository = new HomeRepository(application);
    }

    public LiveData<DayPrayerTimes> getDayPrayer() {
        return repository.getDayPrayerTimes();
    }
    public LiveData<AdkarModel> getRandomDikr() {
        return repository.getRandomDikr();
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
    public void setRandomDikr() {
        repository.setRandomDikr();
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

