package com.app.dz.quranapp.quran.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.app.dz.quranapp.data.room.Entities.Aya;
import com.app.dz.quranapp.quran.hafs_parte.AyatPageRepository;

import java.util.List;


public class AyatPageViewModel extends AndroidViewModel {

    private final AyatPageRepository repository;

    public AyatPageViewModel(@NonNull Application application) {
        super(application);
        repository = new AyatPageRepository(application);
    }

    public LiveData<List<Aya>> getAllAyat() {
        return repository.getAllAyat();
    }
    public LiveData<Aya> getPevAya() {
        return repository.getPrevAya();
    }

    public void setAyatList(int page_number) {
        repository.setAyatList(page_number);
    }
    public void setlastAya(int page_number) {
        repository.setLastAyaInPage(page_number);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.clearDesposite();
    }

}

