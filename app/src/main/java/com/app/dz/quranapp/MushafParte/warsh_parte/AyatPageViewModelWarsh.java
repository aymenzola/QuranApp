package com.app.dz.quranapp.MushafParte.warsh_parte;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.app.dz.quranapp.Entities.Aya;
import com.app.dz.quranapp.MushafParte.hafs_parte.AyatPageRepository;

import java.util.List;


public class AyatPageViewModelWarsh extends AndroidViewModel {

    private final AyatPageRepositoryWarsh repository;

    public AyatPageViewModelWarsh(@NonNull Application application) {
        super(application);
        repository = new AyatPageRepositoryWarsh(application);
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

