package com.app.dz.quranapp.MainFragmentsParte.HomeFragment;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.app.dz.quranapp.Entities.Juz;
import com.app.dz.quranapp.Entities.Sura;

import java.util.ArrayList;
import java.util.List;


public class SuraViewModel extends AndroidViewModel {

    private final SuraRepository repository;
    private List<Sura> all_suraList;

    public SuraViewModel(@NonNull Application application) {
        super(application);
        repository = new SuraRepository(application);
        all_suraList = new ArrayList<>();
    }

    public List<Sura> getAll_suraList() {
        return all_suraList;
    }

    public void setAll_suraList(List<Sura> all_suraList) {
        this.all_suraList.addAll(all_suraList);
    }

    public LiveData<List<Sura>> getAllSura() {
        return repository.getAllSura();
    }

    public LiveData<List<Juz>> getAllJuza() {
        return repository.getAllJuza();
    }

    public void setSuraList(int startId, int pageSize) {
        repository.setSuraList(startId, pageSize);
    }

    public void setAllSuraList() {
        repository.setAllSuraList();
    }

    public void setJuzaList() {
        repository.setJuzaList();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.clearDesposite();
        Log.e(HomeFragment.QURAN_TAG, "sura viewmodel cleared");
    }


}

